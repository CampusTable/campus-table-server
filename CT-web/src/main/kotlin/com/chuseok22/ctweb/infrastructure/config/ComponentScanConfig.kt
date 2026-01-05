package com.chuseok22.ctweb.infrastructure.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [
  "com.chuseok22.*"
])
class ComponentScanConfig {
}