package com.chuseok22.ctauth.application.service

import com.chuseok22.ctauth.application.dto.request.LoginRequest
import com.chuseok22.ctauth.application.dto.response.LoginResponse
import com.chuseok22.ctauth.core.token.TokenProvider
import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import com.chuseok22.ctmember.infrastructure.entity.Member
import com.chuseok22.ctmember.infrastructure.repository.MemberRepository
import com.chuseok22.sejongportallogin.core.SejongMemberInfo
import com.chuseok22.sejongportallogin.infrastructure.SejongPortalLoginService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger { }

@Service
class AuthService(
  private val sejongPortalLoginService: SejongPortalLoginService,
  private val memberRepository: MemberRepository,
  private val tokenProvider: TokenProvider
) {

  @Transactional
  fun login(request: LoginRequest): LoginResponse {
    val sejongMemberInfo = sejongPortalLogin(request.sejongPortalId, request.sejongPortalPw)
    val studentNumber = sejongMemberInfo.studentId
    val name = sejongMemberInfo.name


    val member = memberRepository.findByStudentNumberAndDeletedFalse(studentNumber)
      ?: run {
        log.info { "신규 회원 로그인: 학번=$studentNumber, 이름=$name" }
        val newMember = Member.create(studentNumber, name)
        memberRepository.save(newMember)
      }

    // 토큰 발급
    val accessToken = tokenProvider.createAccessToken(member.id.toString())
    val refreshToken = tokenProvider.createRefreshToken(member.id.toString())

    log.info { "로그인 성공: 학번=$studentNumber, 이름=$name" }

    return LoginResponse(
      studentNumber = studentNumber,
      name = name,
      accessToken = accessToken,
      refreshToken = refreshToken
    )
  }

  private fun sejongPortalLogin(sejongPortalId: String, sejongPortalPw: String): SejongMemberInfo {
    try {
      log.debug { "세종대학교 포털 로그인을 시도합니다: $sejongPortalId" }
      return sejongPortalLoginService.getMemberAuthInfos(sejongPortalId, sejongPortalPw)
    } catch (e: Exception) {
      log.error(e) { "세종대학교 포털 로그인 중 오류 발생: ${e.message}" }
      throw CustomException(ErrorCode.SEJONG_PORTAL_LOGIN_FAILED)
    }
  }
}