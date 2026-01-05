package com.chuseok22.ctweb.infrastructure.config

import com.chuseok22.ctweb.infrastructure.properties.SpringDocProperties
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
  info = Info(
    title = "캠퍼스 테이블 CampusTable"
  )
)
@Configuration
@EnableConfigurationProperties(SpringDocProperties::class)
class SwaggerConfig(
  private val properties: SpringDocProperties
) {

  @Bean
  fun OpenAPI(): OpenAPI {
    val apiKey: SecurityScheme = SecurityScheme()
      .type(SecurityScheme.Type.HTTP)
      .scheme("bearer")
      .bearerFormat("JWT")
      .`in`(SecurityScheme.In.HEADER)
      .name("Authorization")

    return OpenAPI()
      .components(Components().addSecuritySchemes("Bearer Token", apiKey))
      .addSecurityItem(SecurityRequirement().addList("Bearer Token"))
  }

  @Bean
  fun serverCustomizer(): OpenApiCustomizer {
    return OpenApiCustomizer { openApi ->
      properties.servers.forEach { server ->
        openApi.addServersItem(
          Server()
            .url(server.url)
            .description(server.description)
        )
      }
    }
  }
}