package com.warmisland.config;

import io.swagger.v3.oas.info.Info;
import org.springdoc.api.OpenAPI;
import org.springdoc.api.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Warm Island Shop API")
                        .version("v1.0.0")
                        .description("暖屿小店后端 API 文档"));
    }
}
