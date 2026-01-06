package com.chuseok22.ctauth.infrastructure.constant

object AuthConstants {

  // Auth
  const val TOKEN_PREFIX: String = "Bearer "
  const val HEADER_AUTHORIZATION: String = "Authorization"

  // CookieUtil
  const val ROOT_DOMAIN: String = "campustable.shop"
  const val ACCESS_TOKEN_KEY: String = "accessToken"
  const val REFRESH_TOKEN_KEY: String = "refreshToken"

  // JwtUtil
  const val ACCESS_TOKEN_CATEGORY: String = "accessToken"
  const val REFRESH_TOKEN_CATEGORY: String = "refreshToken"
  const val REDIS_REFRESH_TOKEN_KEY_PREFIX: String = "RT:"

}