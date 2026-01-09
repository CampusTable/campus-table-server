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
  fun redisConnectionFactory(): RedisConnectionFactory {

    val config = RedisStandaloneConfiguration().apply {
      hostName = properties.host
      port = properties.port
      setPassword(properties.password)
    }

    return LettuceConnectionFactory(config)
  }

  /**
   * RedisTemplate
   */
  @Bean
  fun redisTemplate(
    factory: RedisConnectionFactory,
    serializer: GenericJackson2JsonRedisSerializer
  ): RedisTemplate<String, Any> {

    val stringSerializer = StringRedisSerializer()

    return RedisTemplate<String, Any>().apply {
      connectionFactory = factory

      // 직렬화 설정
      keySerializer = stringSerializer
      hashKeySerializer = stringSerializer
      valueSerializer = serializer
      hashValueSerializer = serializer

      afterPropertiesSet()
    }
  }

  @Bean
  fun redisSerializer(): GenericJackson2JsonRedisSerializer {
    return GenericJackson2JsonRedisSerializer(createObjectMapper())
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