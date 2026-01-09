package com.chuseok22.ctauth.infrastructure.config

import com.chuseok22.ctauth.infrastructure.jwt.JwtProvider
import com.chuseok22.ctauth.infrastructure.properties.JwtProperties
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.crypto.SecretKey

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtConfig(
  private val properties: JwtProperties
) {

  /**
   * JWT 서명에 사용할 secretKey 생성
   * base64로 인코딩 된 secretKey를 디코딩해서 SecretKey 객체 생성
   */
  @Bean
  fun jwtSecretKey(): SecretKey {
    val keyBytes: ByteArray = Decoders.BASE64.decode(properties.secretKey)
    return Keys.hmacShaKeyFor(keyBytes)
  }

  /**
   * TokenProvider 구현체 Bean 등록
   */
  @Bean
  fun jwtProvider(jwtSecretKey: SecretKey): JwtProvider {
    return JwtProvider(jwtSecretKey(), properties)
  }
}