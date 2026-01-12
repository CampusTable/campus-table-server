package com.chuseok22.ctcommon.application.exception

class CustomException(
  val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)