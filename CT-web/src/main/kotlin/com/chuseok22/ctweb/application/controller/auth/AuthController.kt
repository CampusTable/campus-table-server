package com.chuseok22.ctweb.application.controller.auth

import com.chuseok22.ctauth.application.dto.request.LoginRequest
import com.chuseok22.ctauth.application.dto.request.ReissueRequest
import com.chuseok22.ctauth.application.dto.response.LoginResponse
import com.chuseok22.ctauth.application.dto.response.ReissueResponse
import com.chuseok22.ctauth.application.service.AuthService
import com.chuseok22.logging.annotation.LogMonitoring
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 API")
class AuthController(
  private val authService: AuthService
) {

  @LogMonitoring
  @PostMapping("/login")
  fun login(request: LoginRequest): ResponseEntity<LoginResponse> {
    return ResponseEntity.ok(authService.login(request))
  }

  @LogMonitoring
  @PostMapping("/reissue")
  fun reissue(request: ReissueRequest): ResponseEntity<ReissueResponse> {
    return ResponseEntity.ok(authService.reissue(request))
  }

  @LogMonitoring
  @PostMapping("/logout")
  fun logout(): ResponseEntity<Unit> {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
  }
}