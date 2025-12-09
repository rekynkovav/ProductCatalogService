package com.productCatalogService.swagger;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Аннотация для включения SpringDoc OpenAPI документации.
 * При добавлении к основному классу приложения автоматически настраивает Swagger UI.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see Import
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SwaggerAutoConfiguration.class)
public @interface EnableSwagger {

    /**
     * Определяет, нужно ли автоматически генерировать документацию.
     * По умолчанию - true.
     */
    boolean autoGenerate() default true;

    /**
     * Путь к Swagger UI.
     * По умолчанию - "/swagger-ui.html".
     */
    String uiPath() default "/swagger-ui.html";

    /**
     * Путь к JSON документации API.
     * По умолчанию - "/v3/api-docs".
     */
    String apiDocsPath() default "/v3/api-docs";
}