package com.chuseok22.ctredis.infrastructure.config

import com.chuseok22.ctredis.infrastructure.properties.RedisProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableConfigurationProperties(RedisProperties::class)
class RedisConfig(
  private val properties: RedisProperties
) {

  /**
   * Redis Factory
   */
  @Bean
  fun redisConnectionFactory(redisProperties: RedisProperties): RedisConnectionFactory {

    val config = RedisStandaloneConfiguration().apply {
      hostName = properties.host
      port = properties.port
      setPassword(redisProperties.password)
    }

    return LettuceConnectionFactory(config)
  }

  /**
   * RedisTemplate
   */
  @Bean
  fun redisTemplate(
    connectionFactory: RedisConnectionFactory
  ): RedisTemplate<String, Any> {
    val objectMapper = createObjectMapper()

    return RedisTemplate<String, Any>().apply {
      this.connectionFactory = connectionFactory

      // Key Serializer: String
      keySerializer = StringRedisSerializer()
      hashKeySerializer = StringRedisSerializer()

      // Value Serializer: JSON
      val jsonSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
      valueSerializer = jsonSerializer
      hashValueSerializer = jsonSerializer

      afterPropertiesSet()
    }
  }

  /**
   * ObjectMapper 생성
   */
  private fun createObjectMapper(): ObjectMapper {
    return ObjectMapper().apply {
      // Kotlin 모듈
      registerModule(KotlinModule.Builder().build())

      // Java 8 Time API (LocalDateTime 등)
      registerModule(JavaTimeModule())
      disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

      // 다형성 지원
      activateDefaultTyping(
        BasicPolymorphicTypeValidator.builder()
          .allowIfBaseType(Any::class.java)
          .build(),
        ObjectMapper.DefaultTyping.NON_FINAL
      )
    }
  }
}