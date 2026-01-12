package com.chuseok22.ctstorage.core.constant

import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import com.chuseok22.ctcommon.core.util.nvl

enum class FileExtension(
  val extension: String,
  val contentType: String
) {

  JPG("jpg", "image/jpeg"),
  JPEG("jpeg", "image/jpeg"),
  PNG("png", "image/png"),
  GIF("gif", "image/gif"),
  BMP("bmp", "image/bmp"),
  WEBP("webp", "image/webp");

  companion object {

    /**
     * 파일명에서 확장자 추출
     */
    fun fromFilename(filename: String?): FileExtension {
      val safeFilename = filename.nvl("")
      if (safeFilename.isEmpty()) {
        throw CustomException(ErrorCode.INVALID_FILE_REQUEST)
      }

      val extension = safeFilename
        .lastIndexOf(".")
        .takeIf { it >= 0 && it < safeFilename.length - 1 }
        ?.let { safeFilename.substring(it + 1) }
        ?.lowercase()
        ?: throw CustomException(ErrorCode.INVALID_FILE_EXTENSION)

      return entries.firstOrNull { it.extension == extension }
        ?: throw CustomException(ErrorCode.INVALID_FILE_EXTENSION)
    }

    /**
     * 유효한 파일 확장자 검증
     */
    fun isValidExtension(filename: String?): Boolean {
      return try {
        fromFilename(filename)
        true
      } catch (e: CustomException) {
        false
      }
    }

    /**
     * 확장자에 해당하는 ContentType 반환
     */
    fun getContentTypeByFilename(filename: String?): String {
      return fromFilename(filename).contentType
    }
  }
}