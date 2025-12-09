-- Установка прав доступа
GRANT ALL PRIVILEGES ON SCHEMA app_schema TO productcatalog;
GRANT ALL PRIVILEGES ON SCHEMA seq_schema TO productcatalog;

-- Установка search_path для пользователя
ALTER ROLE productcatalog SET search_path TO app_schema, seq_schema, public;

-- Разрешение на использование sequence
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA seq_schema TO productcatalog;