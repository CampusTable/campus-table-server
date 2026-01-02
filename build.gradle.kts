plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.5.9"
  id("io.spring.dependency-management") version "1.1.7"
  kotlin("plugin.jpa") version "1.9.25"
}

group = "com.chuseok22"
version = "0.0.1-SNAPSHOT"
description = "campus-table-server"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  maven {
    url = uri("https://nexus.chuseok22.com/repository/maven-releases/")
  }
  mavenCentral()
}

dependencies {
  // Spring Web
  implementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.boot:spring-boot-starter-test")

  // JPA
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")

  // Postgres
  runtimeOnly("org.postgresql:postgresql")

  // Redis
  implementation("org.springframework.boot:spring-boot-starter-data-redis")

  // Spring Security
  implementation("org.springframework.boot:spring-boot-starter-security")
  testImplementation("org.springframework.security:spring-security-test")

  // Spring Validation
  implementation("org.springframework.boot:spring-boot-starter-validation")

  // Jackson
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  // Reflect
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  // Lombok
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")

  // JUnit5
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

allOpen {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.MappedSuperclass")
  annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
