package com.chuseok22.ctauth.application.dto.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
  @field:NotBlank
  val sejongPortalId: String,
  @field:NotBlank
  val sejongPortalPw: String
)
