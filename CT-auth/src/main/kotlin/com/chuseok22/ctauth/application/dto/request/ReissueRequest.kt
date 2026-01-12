package com.chuseok22.ctauth.application.dto.request

import jakarta.validation.constraints.NotBlank

data class ReissueRequest(
  @field:NotBlank
  val refreshToken: String
)
