package com.chuseok22.ctcommon.core.util

/**
 * null 또는 빈 문자열을 기본값으로 대체
 */
fun String?.nvl(fallback: String): String {
  return when {
    this == null -> fallback
    this == "null" -> fallback
    this.isBlank() -> fallback
    else -> this
  }
}