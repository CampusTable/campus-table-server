package com.chuseok22.ctcommon.application.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
  val status: HttpStatus,
  val message: String
) {

  // Global
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다"),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),

  // JWT
  INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT"),
  EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT"),

  // Member
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),
}