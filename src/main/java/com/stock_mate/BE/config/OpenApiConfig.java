package com.stock_mate.BE.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("StockMate").version("1.0"))
//                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
//                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
//                .components(new Components()
//                        .addSecuritySchemes("bearerAuth",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")
//                                        .in(SecurityScheme.In.HEADER)
//                                        .name("Authorization"))
//                        .addSecuritySchemes("cookieAuth",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.APIKEY)
//                                        .in(SecurityScheme.In.COOKIE)
//                                        .name("AccessToken")))
                ;
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/v1/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/v2/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v3Api() {
        return GroupedOpenApi.builder()
                .group("v3")
                .pathsToMatch("/v3/**")
                .build();
    }
}
