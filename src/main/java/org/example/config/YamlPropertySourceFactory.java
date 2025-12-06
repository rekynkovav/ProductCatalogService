package org.example.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Properties;

/**
 * Фабрика для загрузки свойств из YAML файлов.
 * Позволяет использовать YAML файлы вместо традиционных .properties файлов.
 *
 * @apiNote Интегрируется с Spring через аннотацию @PropertySource
 * @see PropertySourceFactory
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    /**
     * Создает источник свойств из YAML файла.
     *
     * @param name     имя источника свойств (может быть null)
     * @param resource закодированный ресурс с YAML содержимым
     * @return источник свойств Spring
     * @throws IOException если произошла ошибка чтения ресурса
     * @see YamlPropertiesFactoryBean
     */
    @Override
    public org.springframework.core.env.PropertySource<?> createPropertySource(
            @Nullable String name, EncodedResource resource) throws IOException {

        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        factory.afterPropertiesSet();

        Properties properties = factory.getObject();
        return new PropertiesPropertySource(
                resource.getResource().getFilename(),
                properties != null ? properties : new Properties()
        );
    }
}