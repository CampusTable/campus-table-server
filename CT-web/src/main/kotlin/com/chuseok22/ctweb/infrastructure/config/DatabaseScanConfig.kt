package com.chuseok22.ctweb.infrastructure.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan(basePackages = ["com.chuseok22.*"])
@EnableJpaRepositories(basePackages = ["com.chuseok22.*"])
class DatabaseScanConfig {
}