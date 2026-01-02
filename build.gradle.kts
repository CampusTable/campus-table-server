import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.spring) apply false
  alias(libs.plugins.kotlin.jpa) apply false
  alias(libs.plugins.kotlin.allopen) apply false
  alias(libs.plugins.spring.boot) apply false
  alias(libs.plugins.spring.dependency.management) apply false

}

allprojects {
  group = "com.chuseok22"
  version = "0.0.1-SNAPSHOT"
  description = "campus-table-server"

  repositories {
    maven {
      url = uri("https://nexus.chuseok22.com/repository/maven-releases/")
    }
    mavenCentral()
  }
}

subprojects {
  apply(plugin = "java-library")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")
  apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
  apply(plugin = "org.springframework.boot")
  apply(plugin = "io.spring.dependency-management")

  configure<JavaPluginExtension> {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(21))
    }
  }

  configurations.named("compileOnly") {
    extendsFrom(configurations.named("annotationProcessor").get())
  }

  dependencies {
    add("testImplementation", rootProject.libs.kotlin.test.junit5)
    add("testRuntimeOnly", rootProject.libs.junit.platform.launcher)
  }

  tasks.withType<Test> {
    useJUnitPlatform()
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "21"
      freeCompilerArgs = listOf("-Xjsr305=strict")
    }
  }
}
