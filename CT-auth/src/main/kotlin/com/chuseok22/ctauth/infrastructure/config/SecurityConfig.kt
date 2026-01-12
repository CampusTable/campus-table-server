package com.chuseok22.ctauth.infrastructure.config

import com.chuseok22.ctauth.core.token.TokenProvider
import com.chuseok22.ctauth.infrastructure.constant.SecurityUrls
import com.chuseok22.ctauth.infrastructure.filter.TokenAuthenticationFilter
import com.chuseok22.ctmember.application.MemberService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
  private val tokenProvider: TokenProvider,
  private val memberService: MemberService,
  private val objectMapper: ObjectMapper
) {
  /**
   * SecurityFilterChain 설정
   */
  @Bean
  fun filterChain(http: HttpSecurity, tokenAuthenticationFilter: TokenAuthenticationFilter): SecurityFilterChain {
    return http
      .cors {}
      .csrf { it.disable() }
      .httpBasic { it.disable() }
      .formLogin { it.disable() }

      .authorizeHttpRequests { authorize ->
        authorize
          // AUTH_WHITELIST 에 등록된 URL은 인증 허용
          .requestMatchers(*SecurityUrls.AUTH_WHITELIST.toTypedArray()).permitAll()
          .anyRequest().authenticated()
      }

      // 세션 설정 (STATELESS)
      .sessionManagement { session ->
        session
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }

      .addFilterBefore(
        tokenAuthenticationFilter,
        UsernamePasswordAuthenticationFilter::class.java
      )
      .build()
  }

  @Bean
  fun tokenAuthenticationFilter(): TokenAuthenticationFilter {
    return TokenAuthenticationFilter(tokenProvider, memberService, objectMapper)
  }
}