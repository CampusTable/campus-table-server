plugins {
  id("java-library")
}

tasks.bootJar {
  enabled = false
}

tasks.jar {
  enabled = true
  archiveClassifier.set("")
}

// common 모듈 api 의존성은 모든 모듈에 적용
dependencies {
  // Spring Starter Web
  api(libs.spring.boot.starter.web)

  // JPA
  api(libs.spring.boot.starter.data.jpa)

  // Validation
  api(libs.spring.boot.starter.validation)

  // Jackson
  api(libs.jackson.module.kotlin)

  // Kotlin Logging
  api(libs.kotlin.logging)

  api(libs.kotlin.reflect)
  api(libs.kotlin.stdlib)
}

