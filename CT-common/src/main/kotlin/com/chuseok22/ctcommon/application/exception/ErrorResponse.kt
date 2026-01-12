package com.chuseok22.ctcommon.application.exception

data class ErrorResponse(
  val errorCode: ErrorCode,
  val errorMessage: String
)