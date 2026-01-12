package com.chuseok22.ctauth.infrastructure.user

import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import com.chuseok22.ctmember.infrastructure.repository.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
  private val memberRepository: MemberRepository
) : UserDetailsService {

  @Transactional(readOnly = true)
  override fun loadUserByUsername(username: String): UserDetails {
    val member = memberRepository.findByStudentNumberAndDeletedFalse(username)
      ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND)
    return CustomUserDetails(member)
  }
}