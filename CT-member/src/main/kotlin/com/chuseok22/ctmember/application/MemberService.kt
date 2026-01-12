package com.chuseok22.ctmember.application

import com.chuseok22.ctcommon.application.exception.CustomException
import com.chuseok22.ctcommon.application.exception.ErrorCode
import com.chuseok22.ctmember.infrastructure.entity.Member
import com.chuseok22.ctmember.infrastructure.repository.MemberRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger { }

@Service
class MemberService(
  private val memberRepository: MemberRepository
) {

  @Transactional(readOnly = true)
  fun findMemberById(memberId: UUID): Member {
    return memberRepository.findByIdAndDeletedFalse(memberId)
      ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND)
  }

}