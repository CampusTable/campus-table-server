package com.chuseok22.ctcommon.infrastructure.time

import com.chuseok22.ctcommon.core.time.TimeProvider
import java.time.Clock
import java.time.Instant

class SystemTimeProvider(
  private val clock: Clock
) : TimeProvider {
  override fun now(): Instant {
    return Instant.now(clock)
  }
}