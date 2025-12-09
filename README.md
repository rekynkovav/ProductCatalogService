запускать через docker-compose.yml

AuthController (/auth)

    POST /auth/register – регистрация пользователя

    POST /auth/login – вход пользователя

    POST /auth/logout – выход пользователя

    GET /auth/users/exists/{username} – проверка существования пользователя

ProductController (/products)

    GET /products – получение товаров с пагинацией

    GET /products/{id} – получение товара по ID

    GET /products/category/{categoryId} – получение товаров по категории

CategoryController (/categories)

    GET /categories – получение всех категорий

    GET /categories/{id} – получение категории по ID

UserController (/user)

    GET /user/profile – получение профиля текущего пользователя

    GET /user/basket – получение корзины пользователя

    GET /user/basket/summary – получение сводной информации о корзине

    GET /user/basket/validate – валидация корзины

    POST /user/basket/add/{productId} – добавление товара в корзину

    PUT /user/basket/update/{productId} – обновление количества товара в корзине

    DELETE /user/basket/remove/{productId} – удаление товара из корзины

    DELETE /user/basket/clear – очистка всей корзины

ProductAdminController (/admin/products)

    POST /admin/products – создание товара (админ)

    PUT /admin/products/{id} – обновление товара (админ)

    DELETE /admin/products/{id} – удаление товара (админ)

CategoryAdminController (/admin/categories)

    POST /admin/categories – создание категории (админ)

    PUT /admin/categories/{id} – обновление категории (админ)

    DELETE /admin/categories/{id} – удаление категории (админ)

UserAdminController (/admin/users)

    GET /admin/users – получение всех пользователей (админ)

StatisticsController (/admin/statistics)

    GET /admin/statistics – получение статистики (админ)

Всего: 25 endpoint'ов