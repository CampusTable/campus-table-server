package com.chuseok22.ctauth.core.token

interface TokenStore {

  /**
   * 리프레시 토큰을 주어진 Key로 저장하고 TTL(ms) 설정
   */
  fun save(key: String, refreshToken: String, ttlMillis: Long)

  /**
   * Key에 해당하는 리프레시 토큰 삭제
   */
  fun remove(key: String)
}