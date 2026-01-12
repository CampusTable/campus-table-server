package com.chuseok22.ctauth.infrastructure.jwt

import com.chuseok22.ctauth.core.token.TokenManager
import com.chuseok22.ctauth.core.token.TokenPair
import com.chuseok22.ctauth.core.token.TokenProvider
import com.chuseok22.ctauth.core.token.TokenStore
import com.chuseok22.ctauth.infrastructure.properties.JwtProperties
import com.chuseok22.ctauth.infrastructure.util.AuthUtil
import io.github.oshai.kotlinlogging.KotlinLogging
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
    val key = AuthUtil.getRefreshTokenTtlKey(memberId)
    tokenStore.save(key, refreshToken, jwtProperties.refreshExpMillis)
  }

  override fun removeRefreshTokenTtl(memberId: String) {
    log.debug { "Redis에 저장된 refreshToken을 삭제합니다: 회원=$memberId" }
    val key = AuthUtil.getRefreshTokenTtlKey(memberId)
    tokenStore.remove(key)
  }
}