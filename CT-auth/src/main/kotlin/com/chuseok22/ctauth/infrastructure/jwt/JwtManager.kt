package com.chuseok22.ctauth.infrastructure.jwt

import com.chuseok22.ctauth.core.token.TokenManager
import com.chuseok22.ctauth.core.token.TokenPair
import com.chuseok22.ctauth.core.token.TokenProvider
import com.chuseok22.ctauth.core.token.TokenStore
import com.chuseok22.ctauth.infrastructure.properties.JwtProperties
import com.chuseok22.ctauth.infrastructure.util.AuthUtil
import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Component
class JwtManager(
  private val tokenProvider: TokenProvider,
  private val tokenStore: TokenStore,
  private val jwtProperties: JwtProperties
) : TokenManager {

  override fun createTokenPair(memberId: String): TokenPair {
    return TokenPair(
      accessToken = tokenProvider.createAccessToken(memberId),
      refreshToken = tokenProvider.createRefreshToken(memberId)
    )
  }

  override fun saveRefreshTokenTtl(memberId: String, refreshToken: String) {
    log.debug { "Redis에 refreshToken을 저장합니다" }
    val key = getKey(memberId)
    tokenStore.save(key, refreshToken, jwtProperties.refreshExpMillis)
  }

  override fun removeRefreshTokenTtl(memberId: String) {
    log.debug { "Redis에 저장된 refreshToken을 삭제합니다: 회원=$memberId" }
    val key = getKey(memberId)
    tokenStore.remove(key)
  }

  override fun validateSavedToken(token: String) {

    val memberId = try {
      tokenProvider.getMemberId(token)
    } catch (e: ExpiredJwtException) {
      log.warn(e) { "만료된 refreshToken 사용 시도" }
      throw e
    } catch (e: Exception) {
      log.warn(e) { "유효하지 않은 refreshToken 사용 시도" }
      throw CustomException(ErrorCode.INVALID_JWT)
    }

    val key = getKey(memberId)
    val savedToken = tokenStore.get(key) ?: run {
      log.warn { "Redis에 refreshToken이 존재하지 않습니다: 회원=$memberId" }
      throw CustomException(ErrorCode.INVALID_JWT)
    }

    if (savedToken != token) {
      log.warn { "유효하지 않은 refreshToken 사용 시도: $memberId" }
      throw CustomException(ErrorCode.INVALID_JWT)
    }
  }

  private fun getKey(memberId: String): String {
    return AuthUtil.getRefreshTokenTtlKey(memberId)
  }
}