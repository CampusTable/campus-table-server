plugins {
  id("java-library")
  id("org.springframework.boot")
}

dependencies {
  implementation(project(":CT-auth"))
  implementation(project(":CT-common"))
  implementation(project(":CT-member"))

  implementation(libs.swagger.ui)
  implementation(libs.http.logging)
  implementation(libs.api.change.log)
  implementation(libs.flyway.migration)
  implementation(libs.flyway.database.postgresql)
}

