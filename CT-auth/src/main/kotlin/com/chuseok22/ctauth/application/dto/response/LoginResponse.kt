package com.chuseok22.ctauth.application.dto.response

data class LoginResponse(
  val studentNumber: String,
  val name: String,
  val accessToken: String,
  val refreshToken: String
)
