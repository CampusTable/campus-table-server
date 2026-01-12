package com.chuseok22.ctauth.infrastructure.jwt

import com.chuseok22.ctauth.core.token.TokenStore
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit

class JwtStore(
  private val redisTemplate: RedisTemplate<String, String>
) : TokenStore {

  override fun get(key: String): String? {
    return redisTemplate.opsForValue().get(key)
  }

  override fun save(key: String, refreshToken: String, ttlMillis: Long) {
    redisTemplate.opsForValue().set(key, refreshToken, ttlMillis, TimeUnit.MILLISECONDS)
  }

  override fun remove(key: String) {
    redisTemplate.delete(key)
  }
}