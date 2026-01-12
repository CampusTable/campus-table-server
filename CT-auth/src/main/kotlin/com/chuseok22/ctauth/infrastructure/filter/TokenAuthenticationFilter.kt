package com.chuseok22.ctauth.infrastructure.filter

import com.chuseok22.ctauth.core.token.TokenProvider
import com.chuseok22.ctauth.infrastructure.constant.AuthConstants
import com.chuseok22.ctauth.infrastructure.constant.SecurityUrls
import com.chuseok22.ctauth.infrastructure.user.CustomUserDetails
import com.chuseok22.ctauth.infrastructure.util.AuthUtil
import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import com.chuseok22.ctcommon.application.exception.ErrorResponse
import com.chuseok22.ctmember.application.MemberService
import com.chuseok22.ctmember.core.constant.Role
import com.chuseok22.ctmember.infrastructure.entity.Member
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

private val log = KotlinLogging.logger { }

class TokenAuthenticationFilter(
  private val tokenProvider: TokenProvider,
  private val memberService: MemberService,
  private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

  private val pathMatcher = AntPathMatcher()

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

    val uri = request.requestURI
    val apiRequestType = determineApiRequestType(uri)

    if (isWhitelistedPath(uri)) {
      filterChain.doFilter(request, response)
      return
    }

    try {
      val token = AuthUtil.extractAccessTokenFromRequest(request)
        ?: handleInvalidToken(null)

      if (tokenProvider.isValidToken(token)) {
        handleValidToken(
          request = request,
          response = response,
          filterChain = filterChain,
          token = token,
          apiRequestType = apiRequestType
        )
        return
      } else {
        handleInvalidToken(token)
      }
    } catch (e: CustomException) {
      SecurityContextHolder.clearContext()
      log.error(e) { "[TokenAuthenticationFilter] CustomException 발생: ${e.message}" }
      sendErrorResponse(response, e.errorCode)
      return
    } catch (e: ExpiredJwtException) {
      SecurityContextHolder.clearContext()
      log.error { "만료된 JWT: ${e.message}" }
      sendErrorResponse(response, ErrorCode.EXPIRED_JWT)
      return
    } catch (e: Exception) {
      SecurityContextHolder.clearContext()
      log.error { "인증 처리 중 예외 발생: ${e.message}" }
      sendErrorResponse(response, ErrorCode.UNAUTHORIZED)
      return
    }
  }

  private fun isWhitelistedPath(uri: String): Boolean {
    return SecurityUrls.AUTH_WHITELIST.any { pattern ->
      pathMatcher.match(pattern, uri)
    }.also { isWhitelisted ->
      if (isWhitelisted) {
        log.debug { "인증 생략 경로 요청입니다: $uri 인증을 건너뜁니다." }
      }
    }
  }

  private fun determineApiRequestType(uri: String): ApiRequestType {
    return when {
      uri.startsWith(AuthConstants.API_REQUEST_PREFIX) -> ApiRequestType.API
      uri.startsWith(AuthConstants.ADMIN_REQUEST_PREFIX) -> ApiRequestType.ADMIN
      uri.startsWith(AuthConstants.TEST_REQUEST_PREFIX) -> ApiRequestType.TEST
      else -> {
        log.warn { "요청 uri가 정의되지 않은 API Type 입니다. 요청 URI: $uri" }
        ApiRequestType.OTHER
      }
    }
  }

  /**
   * 유효한 JWT 토큰 처리
   */
  private fun handleValidToken(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain, token: String, apiRequestType: ApiRequestType) {
    val memberId = tokenProvider.getMemberId(token)
    val member = memberService.findMemberById(UUID.fromString(memberId))

    // 관리자 경로 및 권한 검증
    assertAdminAuthenticated(member, apiRequestType)

    val customUserDetails = CustomUserDetails(member)
    val authentication = UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.authorities)

    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

    // Security Context 등록
    SecurityContextHolder.getContext().authentication = authentication

    // 인증성공
    filterChain.doFilter(request, response)
  }

  private fun handleInvalidToken(token: String?): Nothing {
    when {
      token.isNullOrBlank() -> {
        log.error { "토큰이 존재하지 않습니다." }
        throw CustomException(ErrorCode.UNAUTHORIZED)
      }

      else -> {
        log.error { "토큰이 유효하지 않습니다." }
        throw CustomException(ErrorCode.INVALID_JWT)
      }
    }
  }

  /**
   * 관리자 API 접근 권한 체크
   */
  private fun assertAdminAuthenticated(member: Member, apiRequestType: ApiRequestType) {
    if (apiRequestType == ApiRequestType.ADMIN && member.role != Role.ROLE_ADMIN) {
      log.error { "관리자 권한이 없습니다" }
      throw CustomException(ErrorCode.ACCESS_DENIED)
    }
  }

  private fun sendErrorResponse(response: HttpServletResponse, errorCode: ErrorCode) {
    response.apply {
      contentType = MediaType.APPLICATION_JSON_VALUE
      status = errorCode.status.value()
      characterEncoding = "UTF-8"
    }

    val errorResponse = ErrorResponse(
      errorCode = errorCode,
      errorMessage = errorCode.message
    )

    objectMapper.writeValue(response.writer, errorResponse)
  }

  private enum class ApiRequestType {
    API, ADMIN, TEST, OTHER
  }
}