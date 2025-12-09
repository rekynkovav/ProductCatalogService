package com.productCatalogService.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Автоконфигурация для SpringDoc OpenAPI.
 * Активируется при наличии зависимости springdoc-openapi в classpath.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see Configuration
 */
@Configuration
@ConditionalOnClass(name = "org.springdoc.core.SpringDocConfiguration")
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "springdoc", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class SwaggerAutoConfiguration {

    private final SwaggerProperties swaggerProperties;

    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    @Value("${spring.application.name:Product Catalog Service}")
    private String applicationName;

    /**
     * Создает конфигурацию OpenAPI.
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI customOpenAPI() {
        SwaggerProperties.InfoProperties infoProps = swaggerProperties.getInfo();

        return new OpenAPI()
                .info(new Info()
                        .title(infoProps.getTitle())
                        .description(infoProps.getDescription())
                        .version(infoProps.getVersion())
                        .contact(new Contact()
                                .name(infoProps.getContact().getName())
                                .email(infoProps.getContact().getEmail())
                                .url(infoProps.getContact().getUrl()))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description(applicationName + " Server")
                ));
    }
}