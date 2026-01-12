package com.chuseok22.ctauth.infrastructure.constant

object SecurityUrls {

  /**
   * 인증을 생략할 URL 패턴 목록
   */
  @JvmStatic
  val AUTH_WHITELIST = listOf(
    // AUTH
    "/api/auth/login",
    "/api/auth/reissue",

    // Swagger
    "/docs/swagger-ui/**",
    "/v3/api-docs/**",

    // Health Check
    "/actuator/health",
  )
}