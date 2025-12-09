-- Проверяем существование таблиц перед созданием индексов
DO
$$
    BEGIN
        -- Индексы для таблицы products (если таблица существует)
        IF EXISTS (SELECT 1
                   FROM information_schema.tables
                   WHERE table_schema = 'app_schema'
                     AND table_name = 'products') THEN

            -- idx_product_name
            IF NOT EXISTS (SELECT 1
                           FROM pg_indexes
                           WHERE schemaname = 'app_schema'
                             AND tablename = 'products'
                             AND indexname = 'idx_product_name') THEN
                CREATE INDEX idx_product_name ON app_schema.products (name);
            END IF;

            -- idx_product_category_id
            IF NOT EXISTS (SELECT 1
                           FROM pg_indexes
                           WHERE schemaname = 'app_schema'
                             AND tablename = 'products'
                             AND indexname = 'idx_product_category_id') THEN
                CREATE INDEX idx_product_category_id ON app_schema.products (category_id);
            END IF;

            -- idx_product_price
            IF NOT EXISTS (SELECT 1
                           FROM pg_indexes
                           WHERE schemaname = 'app_schema'
                             AND tablename = 'products'
                             AND indexname = 'idx_product_price') THEN
                CREATE INDEX idx_product_price ON app_schema.products (price);
            END IF;
        END IF;

        -- Индексы для таблицы users (если таблица существует)
        IF EXISTS (SELECT 1
                   FROM information_schema.tables
                   WHERE table_schema = 'app_schema'
                     AND table_name = 'users') THEN

            -- idx_user_username (unique)
            IF NOT EXISTS (SELECT 1
                           FROM pg_indexes
                           WHERE schemaname = 'app_schema'
                             AND tablename = 'users'
                             AND indexname = 'idx_user_username') THEN
                CREATE UNIQUE INDEX idx_user_username ON app_schema.users (username);
            END IF;

            -- idx_user_role
            IF NOT EXISTS (SELECT 1
                           FROM pg_indexes
                           WHERE schemaname = 'app_schema'
                             AND tablename = 'users'
                             AND indexname = 'idx_user_role') THEN
                CREATE INDEX idx_user_role ON app_schema.users (role);
            END IF;
        END IF;

        -- Индекс для таблицы categories (если таблица существует)
        IF EXISTS (SELECT 1
                   FROM information_schema.tables
                   WHERE table_schema = 'app_schema'
                     AND table_name = 'categories') THEN

            -- idx_category_name (unique)
            IF NOT EXISTS (SELECT 1
                           FROM pg_indexes
                           WHERE schemaname = 'app_schema'
                             AND tablename = 'categories'
                             AND indexname = 'idx_category_name') THEN
                CREATE UNIQUE INDEX idx_category_name ON app_schema.categories (name);
            END IF;
        END IF;
    END
$$;