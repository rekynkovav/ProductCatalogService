package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;

/**
 * Основной конфигурационный класс Spring приложения.
 * Содержит конфигурацию бинов, настройку веб-слоя и интеграцию с базами данных.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>Конфигурация источника данных (DataSource) с HikariCP</li>
 *   <li>Настройка JdbcTemplate для работы с БД</li>
 *   <li>Конфигурация валидации и транзакций</li>
 *   <li>Регистрация ресурсов Swagger UI</li>
 *   <li>Настройка JSON маппинга с Jackson</li>
 * </ul>
 *
 * @see WebMvcConfigurer
 * @see Configuration
 * @see EnableWebMvc
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@PropertySource(value = "classpath:application.yml", factory = org.example.config.YamlPropertySourceFactory.class)
@ComponentScan(basePackages = "org.example")
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    /**
     * Окружение Spring для доступа к свойствам приложения.
     */
    private final Environment environment;

    /**
     * Создает и настраивает источник данных (DataSource) с использованием HikariCP.
     * Параметры подключения берутся из application.yml.
     *
     * @return настроенный DataSource для подключения к БД
     * @apiNote Используется пул соединений HikariCP для эффективного управления соединениями
     * @see HikariDataSource
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(environment.getProperty("database.url"));
        hikariConfig.setUsername(environment.getProperty("database.username"));
        hikariConfig.setPassword(environment.getProperty("database.password"));
        hikariConfig.setDriverClassName(environment.getProperty("database.driver"));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(
                environment.getProperty("database.maxPoolSize", "10")));
        return new HikariDataSource(hikariConfig);
    }

    /**
     * Создает JdbcTemplate для работы с базой данных.
     *
     * @param dataSource источник данных для подключения к БД
     * @return настроенный JdbcTemplate
     * @see JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Создает менеджер транзакций для управления транзакциями в БД.
     *
     * @param dataSource источник данных для подключения к БД
     * @return менеджер транзакций Spring
     * @see PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * Создает пост-процессор для поддержки валидации на уровне методов.
     *
     * @return пост-процессор валидации методов
     * @apiNote Позволяет использовать аннотации валидации (@Valid) на параметрах методов
     * @see MethodValidationPostProcessor
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    /**
     * Создает фабрику валидаторов для Bean Validation (JSR-380).
     *
     * @return фабрика валидаторов
     * @see LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    /**
     * Настраивает обработчики статических ресурсов для Swagger UI.
     *
     * @param registry реестр обработчиков ресурсов
     * @apiNote Регистрирует маршруты для доступа к ресурсам Swagger
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/webjars/springfox-swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");

        registry.addResourceHandler("/swagger-resources/**")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/v2/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    /**
     * Создает конвертер HTTP сообщений для обработки JSON с Jackson.
     *
     * @return настроенный конвертер Jackson
     * @see MappingJackson2HttpMessageConverter
     */
    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    /**
     * Создает и настраивает ObjectMapper для сериализации/десериализации JSON.
     * Регистрирует модуль для работы с Java 8 Date/Time API.
     *
     * @return настроенный ObjectMapper
     * @apiNote Отключает запись дат как таймстемпов, использует ISO формат
     * @see ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Настраивает простые контроллеры представлений (view controllers).
     *
     * @param registry реестр контроллеров представлений
     * @apiNote Настраивает редиректы для корневого пути и Swagger UI
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/swagger-ui.html");

        registry.addViewController("/swagger-ui").setViewName("redirect:/swagger-ui.html");

        registry.addViewController("/test").setViewName("forward:/static/test.html");

        registry.addViewController("/health").setViewName("forward:/static/health.html");
    }

    /**
     * Настраивает конвертеры HTTP сообщений для Spring MVC.
     *
     * @param converters список конвертеров для настройки
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jackson2HttpMessageConverter());
    }
}