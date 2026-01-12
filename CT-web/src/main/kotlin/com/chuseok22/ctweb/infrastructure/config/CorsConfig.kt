package com.chuseok22.ctweb.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

  @Bean
  fun corsConfigurationSource(): CorsConfigurationSource {
    val configuration = CorsConfiguration().apply {
      allowedOriginPatterns = listOf(
        "https://www.campustable.shop",
        "http://localhost:3000"
      )
      allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
      allowedHeaders = listOf("*")
      allowCredentials = true
      maxAge = 3600L
    }

    return UrlBasedCorsConfigurationSource().apply {
      registerCorsConfiguration("/**", configuration)
    }
  }
}