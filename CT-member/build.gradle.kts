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

  // Postgres
  api(libs.postgresql)
}
