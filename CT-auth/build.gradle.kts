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

dependencies {
  implementation(project(":CT-common"))
  implementation(project(":CT-member"))
  implementation(project(":CT-redis"))

  // Spring Security
  api(libs.spring.boot.starter.security)
  implementation(libs.spring.security.test)

  // JWT
  implementation(libs.jjwt.api)
  runtimeOnly(libs.jjwt.impl)
  runtimeOnly(libs.jjwt.jackson)

  // Sejong Portal Login
  implementation(libs.sejong.portal.login)
}

