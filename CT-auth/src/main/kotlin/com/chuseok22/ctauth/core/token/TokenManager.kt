package com.chuseok22.ctauth.core.token

interface TokenManager {

  /**
   * accessToken, refreshToken Pair 생성
   */
  fun createTokenPair(memberId: String): TokenPair

  /**
   * refreshToken TTL 저장
   */
  fun saveRefreshTokenTtl(memberId: String, refreshToken: String)

  /**
   * refreshToken TTL 삭제
   */
  fun removeRefreshTokenTtl(memberId: String)
}