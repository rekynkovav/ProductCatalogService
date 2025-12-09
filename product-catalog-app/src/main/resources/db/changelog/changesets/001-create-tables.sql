CREATE SCHEMA IF NOT EXISTS service_schema;
CREATE SCHEMA IF NOT EXISTS app_schema;
CREATE SCHEMA IF NOT EXISTS seq_schema;

-- 2. Создаем пользователя (если не существует)
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'productcatalog') THEN
            CREATE USER productcatalog WITH PASSWORD 'productcatalog';
        END IF;
    END
$$;

-- 3. Даем права пользователю на схемы
GRANT ALL PRIVILEGES ON SCHEMA service_schema TO productcatalog;
GRANT ALL PRIVILEGES ON SCHEMA app_schema TO productcatalog;
GRANT ALL PRIVILEGES ON SCHEMA seq_schema TO productcatalog;

-- 4. Устанавливаем владельца схем
ALTER SCHEMA app_schema OWNER TO productcatalog;
ALTER SCHEMA service_schema OWNER TO productcatalog;
ALTER SCHEMA seq_schema OWNER TO productcatalog;

-- 5. Устанавливаем путь поиска для пользователя
DO
$$
    BEGIN
        IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'productcatalog') THEN
            EXECUTE 'ALTER ROLE productcatalog SET search_path TO service_schema, app_schema, seq_schema, public';
        END IF;
    END
$$;

-- 6. Даем права на будущие объекты в схемах
ALTER DEFAULT PRIVILEGES IN SCHEMA service_schema GRANT ALL ON TABLES TO productcatalog;
ALTER DEFAULT PRIVILEGES IN SCHEMA app_schema GRANT ALL ON TABLES TO productcatalog;
ALTER DEFAULT PRIVILEGES IN SCHEMA seq_schema GRANT ALL ON TABLES TO productcatalog;
ALTER DEFAULT PRIVILEGES IN SCHEMA service_schema GRANT ALL ON SEQUENCES TO productcatalog;
ALTER DEFAULT PRIVILEGES IN SCHEMA app_schema GRANT ALL ON SEQUENCES TO productcatalog;
ALTER DEFAULT PRIVILEGES IN SCHEMA seq_schema GRANT ALL ON SEQUENCES TO productcatalog;

-- 7. Создаем таблицы

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS app_schema.users
(
    id       BIGINT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(20)  NOT NULL DEFAULT 'USER'
);

-- Таблица категорий
CREATE TABLE IF NOT EXISTS app_schema.categories
(
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

-- Таблица продуктов
CREATE TABLE IF NOT EXISTS app_schema.products
(
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    quantity    INTEGER        NOT NULL DEFAULT 0,
    price       DECIMAL(10, 2) NOT NULL,
    category_id BIGINT         NOT NULL,
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
            REFERENCES app_schema.categories (id)
            ON DELETE RESTRICT
);