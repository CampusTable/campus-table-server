package com.chuseok22.ctcommon.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.ZoneId

@Configuration
class ZoneConfig {

  @Bean
  fun zoneId(): ZoneId {
    return ZoneId.of("Asia/Seoul")
  }
}