package com.chuseok22.ctstorage.core.model

import com.chuseok22.ctstorage.core.constant.FileExtension

data class FileMetadata(
  val originalFilename: String, // 원본 파일명
  val storedPath: String, // 파일 저장 경로
  val publicUrl: String, // 공개 접근 URL
  val fileExtension: FileExtension, // 파일 확장자
  val sizeBytes: Long // 파일 크기
)
