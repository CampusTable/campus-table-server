package com.chuseok22.ctauth.infrastructure.jwt

import com.chuseok22.ctauth.core.token.TokenProvider
import com.chuseok22.ctauth.infrastructure.constant.AuthConstants
import com.chuseok22.ctauth.infrastructure.properties.JwtProperties
import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.*
import io.jsonwebtoken.security.SignatureException
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

private val log = KotlinLogging.logger { }

class JwtProvider(
  private val secretKey: SecretKey,
  private val properties: JwtProperties
) : TokenProvider {

  override fun createAccessToken(memberId: String, role: String): String {
    return createToken(
      category = AuthConstants.ACCESS_TOKEN_CATEGORY,
      memberId = memberId,
      role = role,
      expMillis = properties.accessExpMillis
    ).also { log.info { "엑세스 토큰 생성완료: memberId = $memberId" } }
  }

  override fun createRefreshToken(memberId: String, role: String): String {
    return createToken(
      category = AuthConstants.REFRESH_TOKEN_CATEGORY,
      memberId = memberId,
      role = role,
      expMillis = properties.refreshExpMillis
    ).also { log.info { "리프레시 토큰 생성완료: memberId = $memberId" } }
  }

  override fun isValidToken(token: String): Boolean {
    return try {
      getClaims(token)
        .also { log.debug { "JWT 토큰이 유효합니다" } }
      true
    } catch (e: ExpiredJwtException) {
      log.warn(e) { "JWT 토큰 만료: ${e.message}" }
      throw e // 만료 예외는 재전달
    } catch (e: UnsupportedJwtException) {
      log.warn(e) { "지원하지 않는 JWT: ${e.message}" }
      false
    } catch (e: MalformedJwtException) {
      log.warn(e) { "형식이 올바르지 않은 JWT: ${e.message}" }
      false
    } catch (e: SignatureException) {
      log.warn(e) { "JWT 서명이 유효하지 않음: ${e.message}" }
      false
    } catch (e: IllegalArgumentException) {
      log.warn(e) { "JWT 토큰이 비어있음: ${e.message}" }
      false
    }
  }

  override fun getMemberId(token: String): String {
    return try {
      getClaims(token)["memberId"] as? String
        ?: throw CustomException(ErrorCode.INVALID_JWT)
    } catch (e: JwtException) {
      log.error(e) { "JWT memberId 추출 실패: ${e.message}" }
      throw e
    }
  }

  override fun getExpiredAt(token: String): Date {
    return try {
      getClaims(token).expiration
    } catch (e: Exception) {
      log.error(e) { "JWT 만료시간 추출 실패: ${e.message}" }
      throw CustomException(ErrorCode.INVALID_JWT)
    }
  }

  private fun createToken(
    category: String,
    memberId: String,
    role: String,
    expMillis: Long
  ): String {
    val now = Instant.now()
    return Jwts.builder()
      .subject(memberId)
      .claim("category", category)
      .claim("role", role)
      .issuer(properties.issuer)
      .issuedAt(Date.from(now))
      .expiration(Date.from(now.plusMillis(expMillis)))
      .signWith(secretKey)
      .compact()
  }

  // 토큰에서 페이로드 (Claim) 추출
  private fun getClaims(token: String): Claims {
    return Jwts.parser()
      .verifyWith(secretKey)
      .build()
      .parseSignedClaims(token)
      .payload
  }

}