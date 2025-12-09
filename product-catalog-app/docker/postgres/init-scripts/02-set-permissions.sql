-- Установка прав доступа
GRANT ALL PRIVILEGES ON SCHEMA service_schema TO productcatalog;
GRANT ALL PRIVILEGES ON SCHEMA app_schema TO productcatalog;
GRANT ALL PRIVILEGES ON SCHEMA seq_schema TO productcatalog;

-- Установка search_path для пользователя
ALTER ROLE productcatalog SET search_path TO app_schema, public;