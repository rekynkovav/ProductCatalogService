-- Создание необходимых схем при инициализации базы данных
CREATE SCHEMA IF NOT EXISTS service_schema;
CREATE SCHEMA IF NOT EXISTS app_schema;
CREATE SCHEMA IF NOT EXISTS seq_schema;

-- Даем права пользователю на схемы
GRANT ALL PRIVILEGES ON SCHEMA service_schema TO productcatalog;
GRANT ALL PRIVILEGES ON SCHEMA app_schema TO productcatalog;
GRANT ALL PRIVILEGES ON SCHEMA seq_schema TO productcatalog;

-- Устанавливаем путь поиска для пользователя
ALTER ROLE productcatalog SET search_path TO service_schema, app_schema, seq_schema, public;

-- Установка прав доступа
GRANT ALL PRIVILEGES ON SCHEMA app_schema TO productcatalog;
GRANT ALL PRIVILEGES ON SCHEMA seq_schema TO productcatalog;

-- Разрешение на использование sequence
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA seq_schema TO productcatalog;