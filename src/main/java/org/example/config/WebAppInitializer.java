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
 * Заменяет традиционный web.xml, настраивая контекст Spring, сервлеты и фильтры.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>Создание корневого и диспетчерского контекстов Spring</li>
 *   <li>Регистрация DispatcherServlet для обработки REST запросов</li>
 *   <li>Настройка фильтров (кодировка UTF-8, CORS)</li>
 *   <li>Регистрация ресурсов Swagger</li>
 * </ul>
 *
 * @see WebApplicationInitializer
 * @see DispatcherServlet
 */
public class WebAppInitializer implements WebApplicationInitializer {

    /**
     * Основной метод инициализации веб-приложения.
     * Вызывается контейнером сервлетов при запуске приложения.
     *
     * @param servletContext контекст сервлета для настройки приложения
     * @throws ServletException если произошла ошибка при инициализации
     * @implNote Настраивает два контекста Spring: корневой и диспетчерский
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("=== Initializing Product Catalog Service ===");

        // Создание корневого контекста Spring
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        servletContext.addListener(new ContextLoaderListener(rootContext));

        // Создание контекста для DispatcherServlet
        AnnotationConfigWebApplicationContext dispatcherContext =
                new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(AppConfig.class);

        // Настройка DispatcherServlet
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

        // Настройка фильтра кодировки UTF-8
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);

        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(
                "encodingFilter",
                encodingFilter
        );
        filterRegistration.addMappingForUrlPatterns(null, false, "/*");

        // Настройка CORS фильтра
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