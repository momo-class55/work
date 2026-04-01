package com.shopqr.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shopQrOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shop QR REST API")
                        .description("태블릿·연동용 REST 엔드포인트 (Swagger UI에서 호출 테스트)")
                        .version("v1"));
    }
}
