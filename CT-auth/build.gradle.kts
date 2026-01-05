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

  api(libs.spring.boot.starter.security)
  api(libs.spring.security.test)
}

