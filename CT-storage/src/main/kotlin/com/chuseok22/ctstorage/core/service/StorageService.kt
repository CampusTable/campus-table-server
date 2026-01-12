package com.chuseok22.ctstorage.core.service

import com.chuseok22.ctstorage.core.constant.UploadType
import com.chuseok22.ctstorage.core.model.FileMetadata
import org.springframework.web.multipart.MultipartFile

interface StorageService {

  /**
   * 파일 업로드
   * - multipartFile 업로드 후 storedPath 반환
   */
  fun upload(file: MultipartFile, uploadType: UploadType): FileMetadata

  /**
   * storedPath로 부터 publicUrl 생성
   */
  fun generatePublicUrl(storedPath: String): String

  /**
   * publicUrl 에서 storedPath 추출
   */
  fun extractStoredPathFromPublicUrl(publicUrl: String): String

  /**
   * 파일 삭제
   */
  fun deleteFile(storedPath: String): Boolean
}