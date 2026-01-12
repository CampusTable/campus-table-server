package com.chuseok22.ctauth.infrastructure.user

import com.chuseok22.ctauth.core.user.UserPrincipal
import com.chuseok22.ctmember.infrastructure.entity.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.security.Principal

class CustomUserDetails(
  private val member: Member
) : UserDetails, UserPrincipal, Principal {
  override fun getAuthorities(): Collection<GrantedAuthority> {
    return listOf(SimpleGrantedAuthority(member.role.name))
  }

  override fun getPassword(): String {
    return ""
  }

  override fun getMemberId(): String {
    return member.id.toString()
  }

  override fun getUsername(): String {
    return member.studentName
  }

  override fun getRoles(): List<String> {
    return listOf(member.role.name)
  }

  override fun getName(): String {
    return member.name
  }
}