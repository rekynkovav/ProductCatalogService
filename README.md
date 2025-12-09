http://localhost:8080/api/v3/api-docs
http://localhost:8080/api/v3/api-docs.yaml
http://localhost:8080/api/v3/api-docs.json

http://localhost:8080/api/swagger-ui/index.html
http://localhost:8080/api/swagger-ui.html

http://localhost:8080/api/actuator/health
http://localhost:8080/api/actuator/info

GET http://localhost:8080/api/categories - Получение всех категорий
GET http://localhost:8080/api/products - Получение всех товаров с пагинацией

🔐 Аутентификация (Authentication)
POST http://localhost:8080/api/auth/register - Регистрация нового пользователя
POST http://localhost:8080/api/auth/login - Вход в систему
POST http://localhost:8080/api/auth/logout - Выход из системы (требует Authorization header)
GET http://localhost:8080/api/auth/users/exists/{username} - Проверка существования пользователя

👤 Пользователь (User)
GET http://localhost:8080/api/user/profile - Получение профиля текущего пользователя (требует Authorization header)
GET http://localhost:8080/api/user/basket - Получение корзины пользователя (требует Authorization header)
POST http://localhost:8080/api/user/basket/add/{productId} - Добавление товара в корзину (требует Authorization header)
DELETE http://localhost:8080/api/user/basket/remove/{productId} - Удаление товара из корзины (требует Authorization header)
DELETE http://localhost:8080/api/user/basket/clear - Очистка корзины пользователя (требует Authorization header)

📁 Категории (Categories) - Публичные
GET http://localhost:8080/api/categories - Получение всех категорий
GET http://localhost:8080/api/categories/{id} - Получение категории по ID
GET http://localhost:8080/api/categories/{id}/products - Получение товаров по категории

🛒 Товары (Products) - Публичные
GET http://localhost:8080/api/products - Получение всех товаров с пагинацией
GET http://localhost:8080/api/products/{id} - Получение товара по ID
GET http://localhost:8080/api/products/category/{categoryId} - Получение товаров по категории (альтернативный путь)

👑 Администратор - Категории (Admin Categories)
POST http://localhost:8080/api/admin/categories - Создание новой категории (только ADMIN)
PUT http://localhost:8080/api/admin/categories/{id} - Обновление категории (только ADMIN)
DELETE http://localhost:8080/api/admin/categories/{id} - Удаление категории (только ADMIN)

👑 Администратор - Товары (Admin Products)
POST http://localhost:8080/api/admin/products - Создание нового товара (только ADMIN)
PUT http://localhost:8080/api/admin/products/{id} - Обновление товара (только ADMIN)
DELETE http://localhost:8080/api/admin/products/{id} - Удаление товара (только ADMIN)

👑 Администратор - Пользователи и статистика
GET http://localhost:8080/api/admin/users - Получение всех пользователей (только ADMIN)
GET http://localhost:8080/api/admin/statistics - Получение статистики (только ADMIN)