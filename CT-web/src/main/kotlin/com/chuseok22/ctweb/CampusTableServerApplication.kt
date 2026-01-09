package com.chuseok22.ctweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class CampusTableServerApplication

fun main(args: Array<String>) {
  runApplication<CampusTableServerApplication>(*args)
}
