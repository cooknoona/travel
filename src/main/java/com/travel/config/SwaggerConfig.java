package com.travel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class SwaggerConfig implements WebMvcConfigurer {
    // localhost:8111/swagger-ui/index.html# (Swagger path)
    @Bean
    public OpenAPI customOpenAPI(){

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // HTTP type
                .scheme("bearer") // Authentication type : bearer
                .bearerFormat("JWT") // Token type : JWT
                .name("Authorization") // HTTP header name
                .in(SecurityScheme.In.HEADER); // Extract token from header

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Management API")
                        .version("v0")
                        .description("API Swagger"))
                .addSecurityItem(securityRequirement) // Added JWT authentication
                .schemaRequirement("JWT", securityScheme); // Security scheme
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(3600); // Cache maintains for
    }
}
