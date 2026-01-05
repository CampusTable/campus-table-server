plugins {
  id("java-library")
  id("org.springframework.boot")
}

dependencies {
  implementation(project(":CT-auth"))
  implementation(project(":CT-common"))
  implementation(project(":CT-member"))

  implementation(libs.swagger.ui)
}

