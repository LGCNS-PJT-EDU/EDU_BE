package com.education.takeit.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    final String securitySchemeName = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");


        return new OpenAPI()
                .info(new Info()
                        .title("takeIT API 문서")
                        .version("1.0")
                        .description("LG CNS Final Project - TakeIT API⭐️"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .schemaRequirement("BearerAuth", securityScheme);
    }
}
