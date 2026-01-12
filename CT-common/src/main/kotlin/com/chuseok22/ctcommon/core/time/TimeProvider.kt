package com.chuseok22.ctcommon.core.time

import java.time.Instant

interface TimeProvider {

  fun now(): Instant
}