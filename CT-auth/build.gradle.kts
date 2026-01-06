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

  // Spring Security
  api(libs.spring.boot.starter.security)
  api(libs.spring.security.test)

  // JWT
  api(libs.jjwt)

  // Sejong Portal Login
  api(libs.sejong.portal.login)
}

