package com.chuseok22.ctauth.application.service

import com.chuseok22.ctauth.application.dto.request.LoginRequest
import com.chuseok22.ctauth.application.dto.request.ReissueRequest
import com.chuseok22.ctauth.application.dto.response.LoginResponse
import com.chuseok22.ctauth.application.dto.response.ReissueResponse
import com.chuseok22.ctauth.core.token.TokenManager
import com.chuseok22.ctauth.core.token.TokenProvider
import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import com.chuseok22.ctmember.infrastructure.entity.Member
import com.chuseok22.ctmember.infrastructure.repository.MemberRepository
import com.chuseok22.sejongportallogin.core.SejongMemberInfo
import com.chuseok22.sejongportallogin.infrastructure.SejongPortalLoginService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger { }

@Service
class AuthService(
  private val sejongPortalLoginService: SejongPortalLoginService,
  private val memberRepository: MemberRepository,
  private val tokenProvider: TokenProvider,
  private val tokenManager: TokenManager
) {

  @Transactional
  fun login(request: LoginRequest): LoginResponse {
    val sejongMemberInfo = sejongPortalLogin(request.sejongPortalId, request.sejongPortalPw)
    val studentNumber = sejongMemberInfo.studentId
    val name = sejongMemberInfo.name


    val member = memberRepository.findByStudentNumberAndDeletedFalse(studentNumber)
      ?: run {
        log.info { "신규 회원 로그인: 학번=$studentNumber, 이름=$name" }
        val newMember = Member.create(studentNumber, name)
        memberRepository.save(newMember)
      }

    // 토큰 발급
    val tokenPair = tokenManager.createTokenPair(member.id.toString())
    tokenManager.saveRefreshTokenTtl(member.id.toString(), tokenPair.refreshToken)

    log.info { "로그인 성공: 학번=$studentNumber, 이름=$name" }

    return LoginResponse(
      studentNumber = studentNumber,
      name = name,
      accessToken = tokenPair.accessToken,
      refreshToken = tokenPair.refreshToken
    )
  }

  fun reissue(request: ReissueRequest): ReissueResponse {
    log.debug { "토큰 재발급을 진행합니다" }
    val memberId = tokenProvider.getMemberId(request.refreshToken)

    tokenManager.validateSavedToken(request.refreshToken)

    log.debug { "새로운 accessToken, refreshToken 발급" }
    val tokenPair = tokenManager.createTokenPair(memberId)
    tokenManager.saveRefreshTokenTtl(memberId, tokenPair.refreshToken)

    return ReissueResponse(
      accessToken = tokenPair.accessToken,
      refreshToken = tokenPair.refreshToken
    )
  }

  fun logout(member: Member) {
    val memberId = member.id
    log.debug { "로그아웃을 진행합니다: 회원=$memberId" }
    log.debug { "기존에 저장된 refreshToken 삭제" }
    tokenManager.removeRefreshTokenTtl(memberId.toString())
  }

  private fun sejongPortalLogin(sejongPortalId: String, sejongPortalPw: String): SejongMemberInfo {
    try {
      log.debug { "세종대학교 포털 로그인을 시도합니다: $sejongPortalId" }
      return sejongPortalLoginService.getMemberAuthInfos(sejongPortalId, sejongPortalPw)
    } catch (e: Exception) {
      log.error(e) { "세종대학교 포털 로그인 중 오류 발생: ${e.message}" }
      throw CustomException(ErrorCode.SEJONG_PORTAL_LOGIN_FAILED)
    }
  }
}