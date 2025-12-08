package com.productcatalogservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI productCatalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Catalog API")
                        .description("REST API для управления каталогом товаров")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Product Catalog Team")
                                .email("support@productcatalog.com")))
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description("Production Server")
                ));
    }
}