package com.chuseok22.ctweb.infrastructure.properties

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "springdoc")
data class SpringDocProperties(
  @field:Valid
  val servers: List<Servers>
) {
  data class Servers(
    @field:NotBlank
    val url: String,
    @field:NotBlank
    val description: String
  )
}
