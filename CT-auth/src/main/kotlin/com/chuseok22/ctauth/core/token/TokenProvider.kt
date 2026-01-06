package com.chuseok22.ctauth.core.token

import java.util.*

interface TokenProvider {

  /**
   * accessToken 생성
   */
  fun createAccessToken(memberId: String, role: String): String

  /**
   * refreshToken 생성
   */
  fun createRefreshToken(memberId: String, role: String): String

  /**
   * 토큰 유효 검사
   */
  fun isValidToken(token: String): Boolean

  /**
   * 토큰에서 memberId 파싱
   */
  fun getMemberId(token: String): String

  /**
   * 토큰 만료시간 반환 (ms)
   */
  fun getExpiredAt(token: String): Date
}