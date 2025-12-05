<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Product Catalog Service</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        .menu {
            display: flex;
            flex-direction: column;
            gap: 15px;
            margin-top: 30px;
        }
        .menu-item {
            padding: 15px;
            background: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            text-align: center;
            transition: background-color 0.3s;
        }
        .menu-item:hover {
            background: #0056b3;
        }
        .info {
            background: #e9ecef;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>🛍️ Product Catalog Service</h1>

    <div class="info">
        <strong>Добро пожаловать в сервис каталога товаров!</strong>
        <p>Это веб-приложение предоставляет функциональность для управления товарами, пользователями и метриками.</p>
    </div>

    <div class="menu">
        <a href="/version" class="menu-item">
            📋 Проверить версию приложения
        </a>

        <div style="margin: 20px 0; text-align: center; color: #666;">
            <em>Дополнительные функции будут добавлены в ближайшее время...</em>
        </div>
    </div>

    <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; text-align: center; color: #888;">
        <p><strong>Технологии:</strong> Java Servlets, JSP, PostgreSQL, Maven</p>
        <p>© 2024 Product Catalog Service</p>
    </div>
</div>
</body>
</html>