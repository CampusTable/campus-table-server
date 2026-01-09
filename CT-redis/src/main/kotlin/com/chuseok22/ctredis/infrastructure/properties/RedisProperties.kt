package com.chuseok22.ctredis.infrastructure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "spring.data.redis")
data class RedisProperties(
  val host: String,
  val port: Int,
  val password: String
) {
}