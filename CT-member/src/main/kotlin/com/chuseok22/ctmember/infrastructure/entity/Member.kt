package com.chuseok22.ctmember.infrastructure.entity

import com.chuseok22.ctcommon.infrastructure.persistence.BaseEntity
import com.chuseok22.ctmember.core.constant.Role
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "member")
open class Member protected constructor() : BaseEntity() {
  @field:Id
  @field:GeneratedValue(strategy = GenerationType.UUID)
  var id: UUID? = null
    protected set

  @field:Column(name = "student_name", nullable = false, unique = true)
  lateinit var studentName: String
    protected set

  @field:Column(name = "name", nullable = false)
  lateinit var name: String
    protected set

  @field:Enumerated(EnumType.STRING)
  @field:Column(name = "role", nullable = false)
  var role: Role = Role.ROLE_USER
    protected set

  private constructor(studentName: String, name: String, role: Role) : this() {
    this.studentName = normalizeStudentName(studentName)
    this.name = normalizeName(name)
    this.role = role
  }

  companion object {
    fun create(studentName: String, name: String): Member {
      return Member(studentName, name, Role.ROLE_USER)
    }

    private fun normalizeStudentName(raw: String): String {
      val normalized: String = raw.trim()
      require(normalized.isNotBlank()) { "학번은 필수로 입력되어야 합니다 " }
      return normalized
    }

    private fun normalizeName(raw: String): String {
      val normalized: String = raw.trim()
      require(normalized.isNotBlank()) { "이름은 필수로 입력되어야 합니다" }
      return normalized
    }
  }
}