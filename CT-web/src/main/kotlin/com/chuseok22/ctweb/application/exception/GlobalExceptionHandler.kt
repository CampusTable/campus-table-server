package com.chuseok22.ctweb.application.exception

import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import com.chuseok22.ctcommon.application.exception.ErrorResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger { }

@RestControllerAdvice
class GlobalExceptionHandler {

  /**
   * 커스텀 예외처리 (CustomException)
   */
  @ExceptionHandler(CustomException::class)
  fun handleCustomException(e: CustomException): ResponseEntity<ErrorResponse> {
    log.error(e) { "CustomException 발생: ${e.message}" }

    return ResponseEntity
      .status(e.errorCode.status)
      .body(
        ErrorResponse(
          errorCode = e.errorCode,
          errorMessage = e.errorCode.message
        )
      )
  }

  /**
   * 그 외 예외처리 (500 에러)
   */
  @ExceptionHandler(Exception::class)
  fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
    log.error { "예상치 못한 예외 발생: ${e.message}" }

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(
        ErrorResponse(
          errorCode = ErrorCode.INTERNAL_SERVER_ERROR,
          errorMessage = ErrorCode.INTERNAL_SERVER_ERROR.message
        )
      )
  }
}