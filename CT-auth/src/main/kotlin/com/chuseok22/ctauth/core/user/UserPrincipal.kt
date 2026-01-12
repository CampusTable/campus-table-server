package com.chuseok22.ctauth.core.user

interface UserPrincipal {

  /**
   * 회원 고유 ID
   */
  fun getMemberId(): String

  /**
   * 로그인 ID
   */
  fun getUsername(): String

  /**
   * 사용자 권한
   */
  fun getRoles(): List<String>
}