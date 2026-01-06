package com.chuseok22.ctauth.infrastructure.properties

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
  @field:NotBlank
  val secretKey: String,
  val accessExpMillis: Long,
  val refreshExpMillis: Long,
  @field:NotBlank
  val issuer: String
)
