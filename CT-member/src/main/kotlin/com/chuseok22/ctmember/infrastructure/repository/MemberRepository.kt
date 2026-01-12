package com.chuseok22.ctmember.infrastructure.repository

import com.chuseok22.ctmember.infrastructure.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member, UUID> {
  fun findByStudentNameAndDeletedFalse(studentName: String): Member?
}