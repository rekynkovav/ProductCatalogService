-- Вставка категорий (только если таблица существует и пустая)
DO
$$
    DECLARE
        categories_count INTEGER;
    BEGIN
        -- Проверяем существование таблицы categories
        IF EXISTS (SELECT 1
                   FROM information_schema.tables
                   WHERE table_schema = 'app_schema'
                     AND table_name = 'categories') THEN

            -- Проверяем, пуста ли таблица
            EXECUTE 'SELECT COUNT(*) FROM app_schema.categories' INTO categories_count;

            IF categories_count = 0 THEN
                -- Вставка категорий
                INSERT INTO app_schema.categories (name, description)
                VALUES ('Электроника', 'Техника и электронные устройства'),
                       ('Одежда', 'Одежда и аксессуары'),
                       ('Книги', 'Книги и литература'),
                       ('Продукты питания', 'Продукты питания');
            END IF;
        END IF;
    END
$$;

-- Вставка продуктов (только если таблица существует и пустая)
DO
$$
    DECLARE
        products_count INTEGER;
    BEGIN
        -- Проверяем существование таблицы products
        IF EXISTS (SELECT 1
                   FROM information_schema.tables
                   WHERE table_schema = 'app_schema'
                     AND table_name = 'products') THEN

            -- Проверяем, пуста ли таблица
            EXECUTE 'SELECT COUNT(*) FROM app_schema.products' INTO products_count;

            IF products_count = 0 THEN
                -- Вставка продуктов
                INSERT INTO app_schema.products (name, description, quantity, price, category_id)
                SELECT 'Смартфон Samsung Galaxy', 'Современный смартфон', 50, 29999.00, id
                FROM app_schema.categories
                WHERE name = 'Электроника';

                INSERT INTO app_schema.products (name, description, quantity, price, category_id)
                SELECT 'Ноутбук Lenovo ThinkPad', 'Надежный бизнес-ноутбук', 30, 89999.00, id
                FROM app_schema.categories
                WHERE name = 'Электроника';

                INSERT INTO app_schema.products (name, description, quantity, price, category_id)
                SELECT 'Футболка мужская', 'Хлопковая футболка', 100, 1999.00, id
                FROM app_schema.categories
                WHERE name = 'Одежда';

                INSERT INTO app_schema.products (name, description, quantity, price, category_id)
                SELECT 'Джинсы женские', 'Современные джинсы', 75, 3499.00, id
                FROM app_schema.categories
                WHERE name = 'Одежда';

                INSERT INTO app_schema.products (name, description, quantity, price, category_id)
                SELECT 'Война и мир (Л.Н. Толстой)', 'Классическая литература', 40, 599.00, id
                FROM app_schema.categories
                WHERE name = 'Книги';

                INSERT INTO app_schema.products (name, description, quantity, price, category_id)
                SELECT 'Мастер и Маргарита (М.А. Булгаков)', 'Культовый роман', 35, 499.00, id
                FROM app_schema.categories
                WHERE name = 'Книги';

                INSERT INTO app_schema.products (name, description, quantity, price, category_id)
                SELECT 'Хлеб ржаной', 'Свежий ржаной хлеб', 200, 89.00, id
                FROM app_schema.categories
                WHERE name = 'Продукты питания';

                INSERT INTO app_schema.products (name, description, quantity, price, category_id)
                SELECT 'Молоко 3,2%', 'Свежее молоко', 150, 99.00, id
                FROM app_schema.categories
                WHERE name = 'Продукты питания';
            END IF;
        END IF;
    END
$$;

-- Вставка пользователей (только если таблица существует и пустая)
DO
$$
    DECLARE
        users_count INTEGER;
    BEGIN
        -- Проверяем существование таблицы users
        IF EXISTS (SELECT 1
                   FROM information_schema.tables
                   WHERE table_schema = 'app_schema'
                     AND table_name = 'users') THEN

            -- Проверяем, пуста ли таблица
            EXECUTE 'SELECT COUNT(*) FROM app_schema.users' INTO users_count;

            IF users_count = 0 THEN
                -- Вставка пользователей
                INSERT INTO app_schema.users (username, password, role)
                VALUES ('admin', '$2a$12$ABC123def456ghi789jkl0', 'ADMIN'),
                       ('user1', '$2a$12$xyz789abc456def123ghi0', 'USER');
            END IF;
        END IF;
    END
$$;