package org.example.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Arrays;

/**
 * Программный инициализатор веб-приложения для Spring.
 */
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("=== Initializing Product Catalog Service ===");

        // 1. Создаем корневой контекст Spring
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        // 2. Добавляем слушатель для управления жизненным циклом контекста
        servletContext.addListener(new ContextLoaderListener(rootContext));

        // 3. Создаем контекст для DispatcherServlet (для контроллеров)
        AnnotationConfigWebApplicationContext dispatcherContext =
                new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(AppConfig.class);

        // 4. Регистрируем DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(dispatcherContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "dispatcher",
                dispatcherServlet
        );
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/api/*");
        dispatcher.addMapping("/swagger-ui/*");
        dispatcher.addMapping("/webjars/*");
        dispatcher.addMapping("/v2/api-docs");
        dispatcher.addMapping("/swagger-resources/*");
        dispatcher.addMapping("/swagger-resources");
        dispatcher.addMapping("/csrf");

        // 5. Настраиваем фильтр для UTF-8 кодировки
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);

        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(
                "encodingFilter",
                encodingFilter
        );
        filterRegistration.addMappingForUrlPatterns(null, false, "/*");

        // 6. Настраиваем CORS фильтр
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistration.Dynamic corsFilter = servletContext.addFilter(
                "corsFilter",
                new CorsFilter(source)
        );
        corsFilter.addMappingForUrlPatterns(null, false, "/*");

        System.out.println("=== DispatcherServlet registered at /api/* ===");
        System.out.println("=== Product Catalog Service initialization complete ===");
    }
}