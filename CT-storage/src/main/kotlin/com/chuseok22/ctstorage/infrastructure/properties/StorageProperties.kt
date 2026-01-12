package com.chuseok22.ctstorage.infrastructure.properties

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "storage")
data class StorageProperties(
  @field:NotBlank
  val rootDir: String,
  val path: Path
) {
  data class Path(
    @field:NotBlank
    val member: String,
    @field:NotBlank
    val menu: String
  )
}
