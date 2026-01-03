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
  implementation(project(":CT-auth"))
  implementation(project(":CT-common"))
  implementation(project(":CT-member"))

  implementation(libs.swagger.ui)
}

