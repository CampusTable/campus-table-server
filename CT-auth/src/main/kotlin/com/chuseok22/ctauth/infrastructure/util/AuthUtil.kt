package com.chuseok22.ctauth.infrastructure.util

import com.chuseok22.ctauth.infrastructure.constant.AuthConstants
import jakarta.servlet.http.HttpServletRequest

object AuthUtil {

  /**
   * HTTP 요청에서 accessToken 추출
   * - null or value 반환 (empty, blank 는 null 반환)
   */
  fun extractAccessTokenFromRequest(request: HttpServletRequest): String? {
    val bearerToken = request.getHeader(AuthConstants.HEADER_AUTHORIZATION)
    return extractTokenWithoutBearer(bearerToken)
  }

  fun getRefreshTokenTtlKey(memberId: String): String {
    return "${AuthConstants.REDIS_REFRESH_TOKEN_KEY_PREFIX}$memberId"
  }

  private fun extractTokenWithoutBearer(bearerToken: String?): String? {
    return bearerToken
      ?.takeIf { it.startsWith(AuthConstants.TOKEN_PREFIX) }
      ?.removePrefix(AuthConstants.TOKEN_PREFIX)
      ?.trim()
      ?.takeIf { it.isNotBlank() }
  }
}