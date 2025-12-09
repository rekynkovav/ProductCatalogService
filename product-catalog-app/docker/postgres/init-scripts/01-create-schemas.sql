-- Создание схем при инициализации базы данных
CREATE SCHEMA IF NOT EXISTS service_schema;
CREATE SCHEMA IF NOT EXISTS app_schema;
CREATE SCHEMA IF NOT EXISTS seq_schema;

-- Установка комментариев
COMMENT ON SCHEMA service_schema IS 'Служебные таблицы приложения (Liquibase)';
COMMENT ON SCHEMA app_schema IS 'Основные данные приложения';
COMMENT ON SCHEMA seq_schema IS 'Sequence для генерации ID';