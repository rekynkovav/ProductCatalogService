Swagger UI: http://localhost:8080/ProductCatalogService-1.0-SNAPSHOT/swagger-ui.html
OpenAPI JSON: http://localhost:8080/ProductCatalogService-1.0-SNAPSHOT/v3/api-docs

endpoint в контроллере:
http://localhost:8080/ProductCatalogService-1.0-SNAPSHOT/
🔐 Аутентификация (Authentication)
POST /api/auth/register - Регистрация нового пользователя
POST /api/auth/login - Вход в систему
POST /api/auth/logout - Выход из системы (требует Authorization header)
GET /api/auth/users/exists/{username} - Проверка существования пользователя

👤 Пользователь (User)
GET /api/user/profile - Получение профиля текущего пользователя (требует Authorization header)
GET /api/user/basket - Получение корзины пользователя (требует Authorization header)
POST /api/user/basket/add/{productId} - Добавление товара в корзину (требует Authorization header)
DELETE /api/user/basket/remove/{productId} - Удаление товара из корзины (требует Authorization header)
DELETE /api/user/basket/clear - Очистка корзины пользователя (требует Authorization header)

📁 Категории (Categories) - Публичные
GET /api/categories - Получение всех категорий
GET /api/categories/{id} - Получение категории по ID
GET /api/categories/{id}/products - Получение товаров по категории

🛒 Товары (Products) - Публичные
GET /api/products - Получение всех товаров с пагинацией
GET /api/products/{id} - Получение товара по ID
GET /api/products/category/{categoryId} - Получение товаров по категории (альтернативный путь)

👑 Администратор - Категории (Admin Categories)
POST /api/admin/categories - Создание новой категории (только ADMIN)
PUT /api/admin/categories/{id} - Обновление категории (только ADMIN)
DELETE /api/admin/categories/{id} - Удаление категории (только ADMIN)

👑 Администратор - Товары (Admin Products)
POST /api/admin/products - Создание нового товара (только ADMIN)
PUT /api/admin/products/{id} - Обновление товара (только ADMIN)
DELETE /api/admin/products/{id} - Удаление товара (только ADMIN)

👑 Администратор - Пользователи и статистика
GET /api/admin/users - Получение всех пользователей (только ADMIN)
GET /api/admin/statistics - Получение статистики (только ADMIN)