#!/bin/bash

set -e  # Остановка при ошибках

echo "=== Начало инициализации БД productcatalog ==="

# Параметры
DB_NAME="productcatalog"
DB_USER="productcatalog"
DB_PASSWORD="productcatalog"

# Ждем запуска PostgreSQL (внутри контейнера)
until pg_isready -U postgres; do
  echo "Ожидание запуска PostgreSQL..."
  sleep 2
done

# 1. Создание БД и пользователя
echo "Создание базы данных '$DB_NAME' и пользователя '$DB_USER'..."
psql -v ON_ERROR_STOP=1 -U postgres <<-EOSQL
    CREATE DATABASE $DB_NAME;
    CREATE USER $DB_USER WITH
        PASSWORD '$DB_PASSWORD'
        NOSUPERUSER
        NOCREATEDB
        NOCREATEROLE;
    GRANT CONNECT ON DATABASE $DB_NAME TO $DB_USER;
EOSQL

# 2. Создание схем внутри БД
echo "Создание схем в базе данных '$DB_NAME'..."
psql -v ON_ERROR_STOP=1 -U postgres -d $DB_NAME <<-EOSQL
    CREATE SCHEMA IF NOT EXISTS entity;
    CREATE SCHEMA IF NOT EXISTS service;

    GRANT ALL PRIVILEGES ON SCHEMA entity TO $DB_USER;
    GRANT ALL PRIVILEGES ON SCHEMA service TO $DB_USER;

    GRANT CREATE ON SCHEMA entity TO $DB_USER;
    GRANT CREATE ON SCHEMA service TO $DB_USER;

    ALTER USER $DB_USER SET search_path TO entity, service;

EOSQL

echo "=== Инициализация БД productcatalog завершена ==="
echo "Данные для подключения:"
echo "  Хост: localhost:5433"
echo "  БД: $DB_NAME"
echo "  Пользователь: $DB_USER"
echo "  Пароль: $DB_PASSWORD"
echo "  Схемы по умолчанию: entity, service"