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

  // Auth
  SEJONG_PORTAL_LOGIN_FAILED(HttpStatus.BAD_REQUEST, "세종대학교 포털 로그인 실패"),

  // Storage
  INVALID_FILE_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 파일 요청입니다"),
  INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 확장자입니다"),
  DIRECTORY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드를 위한 디렉토리를 찾을 수 없습니다"),
  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다"),
  FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "파일을 찾을 수 없습니다"),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다"),
}