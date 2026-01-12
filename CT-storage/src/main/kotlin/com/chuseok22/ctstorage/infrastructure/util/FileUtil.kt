package com.chuseok22.ctstorage.infrastructure.util

import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object FileUtil {

  private val PERCENT_ENCODED_REGEX = Regex("(%[0-9A-Fa-f]{2})")

  /**
   * MultipartFile이 null이거나 빈 파일인지 체크
   */
  fun isNullOrEmpty(file: MultipartFile?): Boolean {
    return file == null || file.isEmpty || file.originalFilename == null
  }

  /**
   * 1. rawPath 정규화
   * 2. baseUrl과 결합
   * 3. 경로 세그먼트 단위로 UTF-8 percent-encoding 수행(%XX는 보존)
   */
  fun buildNormalizedAndEncodedUrl(baseUrl: String, rawPath: String?): String {
    val normalizedPath = normalizePath(rawPath)
    val encodedPath = encodePathSegments(normalizedPath)
    return combineBaseAndPath(baseUrl, encodedPath)
  }

  /**
   * 사용자 입력 경로 정규화
   * 1. 앞 뒤 공백 제거
   * 2. 백슬래시 ('\')를 슬래시('/')로 변환
   * 3. 중복 슬래시("//")를 단일 슬래시로 축소
   * 4. 루트를 나타내는 "/"는 빈 문자열로 변환
   * 5. 절대 경로로 변환: 선행 슬래시('/') 추가
   * 6. 불필요한 후행 슬래시('/') 제거
   *
   * @return 정규화된 경로 ("/webdav")
   */
  fun normalizePath(rawPath: String?): String {
    if (rawPath == null) {
      return ""
    }

    var path = rawPath.trim().replace('\\', '/')
    path = path.replace(Regex("/+"), "/")

    if (path == "/") {
      return ""
    }
    if (!path.startsWith("/")) {
      path = "/$path"
    }
    if (path.endsWith("/") && path.length > 1) {
      path = path.substring(0, path.length - 1)
    }
    return path
  }

  /**
   * BASE URL과 경로를 결합
   *
   * @param baseUrl WebDAV 서버의 베이스 URL (후행 슬래시 제거)
   * @param path    경로
   * @return 결합된 URL
   */
  fun combineBaseAndPath(baseUrl: String, path: String?): String {
    val base = removeTrailingSlash(baseUrl)
    if (path.isNullOrEmpty()) {
      return base
    }
    return if (path.startsWith("/")) {
      base + path
    } else {
      "$base/$path"
    }
  }

  /**
   * UTF-8 인코딩
   *
   * Java URLEncoder는 공백을 '+'로 바꾸므로 '%20'으로 치환
   */
  private fun encodeString(input: String?): String {
    if (input.isNullOrEmpty()) {
      return ""
    }
    return URLEncoder.encode(input, StandardCharsets.UTF_8).replace("+", "%20")
  }

  /**
   * 경로 세그먼트 중 인코딩되지 않은 부분만 인코딩 (%XX는 그대로 유지)
   */
  fun encodePathSegments(path: String?): String {
    if (path.isNullOrEmpty()) {
      return ""
    }

    val segments = path.split("/")
    val builder = StringBuilder()

    for (segment in segments) {
      if (segment.isEmpty()) {
        continue
      }
      builder.append("/")
      builder.append(encodeSegmentPreservingPercents(segment))
    }

    return builder.toString()
  }

  /**
   * URL 또는 경로 문자열의 끝에 있는 슬래시('/') 제거
   */
  fun removeTrailingSlash(url: String?): String {
    if (url.isNullOrEmpty()) {
      return url ?: ""
    }
    return if (url.endsWith("/")) url.substring(0, url.length - 1) else url
  }

  /**
   * 세그먼트 내부에서 이미 percent-encoding된 "%XX"는 유지하고
   * 그 외 구간만 인코딩
   */
  private fun encodeSegmentPreservingPercents(segment: String): String {
    val matches = PERCENT_ENCODED_REGEX.findAll(segment)

    var lastIndex = 0
    val out = StringBuilder()

    for (match in matches) {
      val start = match.range.first
      val endExclusive = match.range.last + 1

      // 1) [lastIndex..start) 구간은 인코딩
      val literal = segment.substring(lastIndex, start)
      if (literal.isNotEmpty()) {
        out.append(encodeString(literal))
      }

      // 2) %XX 부분은 그대로 append
      out.append(match.value)

      lastIndex = endExclusive
    }

    // 3) 남은 tail 구간 인코딩
    val tail = segment.substring(lastIndex)
    if (tail.isNotEmpty()) {
      out.append(encodeString(tail))
    }

    return out.toString()
  }
}