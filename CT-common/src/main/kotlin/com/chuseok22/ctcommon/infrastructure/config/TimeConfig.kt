package com.chuseok22.ctcommon.infrastructure.config

import com.chuseok22.ctcommon.core.time.TimeProvider
import com.chuseok22.ctcommon.infrastructure.time.SystemTimeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class TimeConfig {

  @Bean
  fun utcClock(): Clock {
    return Clock.systemUTC()
  }

  @Bean
  fun timeProvider(clock: Clock): TimeProvider {
    return SystemTimeProvider(clock)
  }
}