package com.chuseok22.ctstorage.infrastructure.service

import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import com.chuseok22.ctstorage.core.constant.FileExtension
import com.chuseok22.ctstorage.core.constant.UploadType
import com.chuseok22.ctstorage.core.model.FileMetadata
import com.chuseok22.ctstorage.core.service.StorageService
import com.chuseok22.ctstorage.infrastructure.properties.StorageProperties
import com.chuseok22.ctstorage.infrastructure.util.FileUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.fileSize

private val log = KotlinLogging.logger { }

@Service
class NfsFileService(
  private val properties: StorageProperties,
  private val zoneId: ZoneId
) : StorageService {
  companion object {
    private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
  }

  @Transactional
  override fun upload(file: MultipartFile, uploadType: UploadType): FileMetadata {
    // 파일 검증
    validateFile(file)

    // 원본 파일명 추출
    val originalFilename = extractOriginalFilename(file)

    // 파일 확장자 검증
    val fileExtension = FileExtension.fromFilename(originalFilename)

    // 파일 prefix 설정
    val prefix = determinePrefix(uploadType)

    // 고유 파일명 생성
    val storedPath = generateStoredPath(originalFilename, prefix)

    // 실제 저장 경로 (절대 경로)
    val targetPath = resolveAbsolutePathFromStoredPath(storedPath)

    // 디렉토리 생성
    ensureDirectoryExists(targetPath)

    // 파일 저장
    saveFile(file, targetPath)

    // publicUrl 생성
    val publicUrl = generatePublicUrl(storedPath)

    log.info { "파일 업로드 성공: storedPath=$storedPath, publicUrl=$publicUrl" }

    return FileMetadata(
      originalFilename = originalFilename,
      storedPath = storedPath,
      publicUrl = publicUrl,
      fileExtension = fileExtension,
      sizeBytes = targetPath.fileSize()
    )
  }

  override fun generatePublicUrl(storedPath: String): String {
    return FileUtil.combineBaseAndPath(properties.baseUrl, storedPath)
  }

  override fun extractStoredPathFromPublicUrl(publicUrl: String): String {
    val baseUrl = FileUtil.removeTrailingSlash(properties.baseUrl)
    if (!publicUrl.startsWith(baseUrl)) {
      log.error { "publicUrl이 baseUrl로 시작하지 않습니다: publicUrl=$publicUrl, baseUrl=${baseUrl}" }
      throw CustomException(ErrorCode.INVALID_FILE_REQUEST)
    }

    // baseUrl 제거, storedPath 추출
    val storedPath = publicUrl.substring(baseUrl.length)
    return FileUtil.normalizePath(storedPath).removePrefix("/")
  }

  override fun deleteFile(storedPath: String): Boolean {
    try {
      val targetPath = resolveAbsolutePathFromStoredPath(storedPath)

      if (!Files.exists(targetPath)) {
        log.warn { "삭제하려는 파일이 존재하지 않습니다: $storedPath" }
        throw CustomException(ErrorCode.FILE_NOT_FOUND)
      }

      Files.delete(targetPath)
      log.info { "파일 삭제 성공: $storedPath" }

      return true
    } catch (e: IOException) {
      // 파일 삭제 실패 시 로그만 출력 후 진행
      log.error(e) { "파일 삭제 중 오류 발생: $storedPath" }
      return false
    }
  }

  private fun validateFile(file: MultipartFile) {
    // 파일 검증
    if (FileUtil.isNullOrEmpty(file)) {
      log.warn { "파일이 비어있거나 존재하지 않습니다" }
      throw CustomException(ErrorCode.INVALID_FILE_REQUEST)
    }
  }

  /**
   * 원본 파일명 반환
   */
  private fun extractOriginalFilename(file: MultipartFile): String {
    val originalFilename = file.originalFilename?.trim().orEmpty()
    if (originalFilename.isEmpty()) {
      log.error { "원본 파일명이 비어있거나 존재하지 않습니다" }
      throw CustomException(ErrorCode.INVALID_FILE_REQUEST)
    }
    return originalFilename
  }

  /**
   * UploadType 에 따른 prefix
   */
  private fun determinePrefix(type: UploadType): String {
    return when (type) {
      UploadType.MEMBER -> properties.path.member
      UploadType.MENU -> properties.path.menu
    }
  }

  /**
   * storedPath 생성 (prefix/yyyyMMdd-UUID-파일명.jpg)
   */
  private fun generateStoredPath(originalFilename: String, prefix: String): String {
    val datePart = ZonedDateTime.now(zoneId).format(DATE_TIME_FORMATTER)

    // 파일명에서 경로 구분자와 특수 문자 제거 + 공백류를 언더스코어로 치환
    val sanitizedFilename = originalFilename
      .replace(Regex("""[/\\:*?"<>|]"""), "_")
      .replace(Regex("""\s+"""), "_")

    return "$prefix/$datePart-${UUID.randomUUID()}-$sanitizedFilename"
  }

  /**
   * storedPath -> 절대 파일 경로 변환 + 경로 조작 방지
   */
  private fun resolveAbsolutePathFromStoredPath(storedPath: String): Path {
    val baseDirPath = Path.of(properties.rootDir).normalize().toAbsolutePath()
    val resolved = baseDirPath.resolve(storedPath).normalize()

    if (!resolved.startsWith(baseDirPath)) {
      log.error { "허용되지 않은 파일 경로 접근 시도: storedPath=$storedPath, resolved=$resolved" }
      throw CustomException(ErrorCode.INVALID_FILE_REQUEST)
    }
    return resolved
  }

  /**
   * 디렉토리 생성 (존재하지 않을 경우)
   */
  private fun ensureDirectoryExists(directory: Path) {
    try {
      if (!Files.exists(directory)) {
        Files.createDirectories(directory)
        log.debug { "디렉토리 생성: $directory" }
      }
    } catch (e: IOException) {
      log.error(e) { "디렉토리 생성 실패: $directory" }
      throw CustomException(ErrorCode.DIRECTORY_NOT_FOUND)
    }
  }

  /**
   * 파일 저장 (NFS 경로)
   */
  private fun saveFile(file: MultipartFile, targetPath: Path) {
    try {
      file.inputStream.use { inputStream ->
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
      }
      log.debug { "파일 저장 완료: $targetPath" }
    } catch (e: IOException) {
      log.error(e) { "파일 저장 실패: $targetPath" }
      throw CustomException(ErrorCode.FILE_UPLOAD_FAILED)
    }
  }
}