-- 8. Разрешение на использование существующих sequence
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA seq_schema TO productcatalog;

-- Создание последовательностей (только если не существуют)
DO
$$
    BEGIN
        -- Проверяем и создаем sequence для categories
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.sequences
                       WHERE sequence_schema = 'seq_schema'
                         AND sequence_name = 'categories_id_seq') THEN
            CREATE SEQUENCE seq_schema.categories_id_seq
                START WITH 1
                INCREMENT BY 1;
        END IF;

        -- Проверяем и создаем sequence для products
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.sequences
                       WHERE sequence_schema = 'seq_schema'
                         AND sequence_name = 'products_id_seq') THEN
            CREATE SEQUENCE seq_schema.products_id_seq
                START WITH 1
                INCREMENT BY 1;
        END IF;

        -- Проверяем и создаем sequence для users
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.sequences
                       WHERE sequence_schema = 'seq_schema'
                         AND sequence_name = 'users_id_seq') THEN
            CREATE SEQUENCE seq_schema.users_id_seq
                START WITH 1
                INCREMENT BY 1;
        END IF;
    END
$$;

-- Назначение последовательностей как значений по умолчанию для первичных ключей
-- Проверяем, что колонки еще не имеют DEFAULT значения
DO
$$
    BEGIN
        -- Для categories
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.columns
                       WHERE table_schema = 'app_schema'
                         AND table_name = 'categories'
                         AND column_name = 'id'
                         AND column_default IS NOT NULL) THEN
            ALTER TABLE app_schema.categories
                ALTER COLUMN id
                    SET DEFAULT nextval('seq_schema.categories_id_seq');
        END IF;

        -- Для products
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.columns
                       WHERE table_schema = 'app_schema'
                         AND table_name = 'products'
                         AND column_name = 'id'
                         AND column_default IS NOT NULL) THEN
            ALTER TABLE app_schema.products
                ALTER COLUMN id
                    SET DEFAULT nextval('seq_schema.products_id_seq');
        END IF;

        -- Для users
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.columns
                       WHERE table_schema = 'app_schema'
                         AND table_name = 'users'
                         AND column_name = 'id'
                         AND column_default IS NOT NULL) THEN
            ALTER TABLE app_schema.users
                ALTER COLUMN id
                    SET DEFAULT nextval('seq_schema.users_id_seq');
        END IF;
    END
$$;