# Troubleshooting Guide
## Smart Expense Tracker

## Проблема: API повертає 404 (Not Found)

### Симптоми
```
POST http://localhost:8080/api/categories 404 (Not Found)
```

### Можливі причини та рішення

#### 1. Backend не запустився

**Перевірка:**
```bash
# Перевірка чи працює контейнер
docker ps | grep expense-tracker-backend

# Або перевірка порту
curl http://localhost:8080/actuator/health
```

**Очікувана відповідь:**
```json
{"status":"UP"}
```

**Якщо не працює:**
```bash
# Подивитися логи
docker logs expense-tracker-backend

# Перезапустити backend
docker-compose restart backend
```

#### 2. База даних не підключилась

**Симптоми в логах:**
```
Could not connect to database
Connection refused
```

**Рішення:**
```bash
# Перевірка PostgreSQL
docker logs expense-tracker-db

# Перевірка підключення
docker exec expense-tracker-db pg_isready -U postgres

# Якщо не працює - перезапустити
docker-compose restart postgres
docker-compose restart backend
```

#### 3. Endpoints не зареєстровані

**Перевірка доступних endpoints:**
```bash
curl http://localhost:8080/swagger-ui.html
```

**Якщо Swagger не відкривається:**
- Backend не стартував правильно
- Перевірте логи: `docker logs expense-tracker-backend`

#### 4. CORS проблеми

**Симптоми в браузері:**
```
Access to XMLHttpRequest blocked by CORS policy
```

**Перевірка конфігурації:**
Файл `backend/src/main/resources/application.properties`:
```properties
spring.web.cors.allowed-origins=http://localhost:3000
```

**Якщо змінили порт frontend:**
- Оновіть `allowed-origins`
- Перезапустіть backend

#### 5. Порти вже зайняті

**Перевірка:**
```bash
# Linux/Mac
netstat -tulpn | grep 8080
lsof -i :8080

# Windows
netstat -ano | findstr :8080
```

**Рішення:**
- Зупиніть процес що використовує порт
- АБО змініть порт в docker-compose.yml

## Проблема: Frontend не завантажується

### 1. Білий екран

**Причини:**
- JavaScript помилки
- Build failed

**Рішення:**
```bash
# Перевірка логів
docker logs expense-tracker-frontend

# Перебудова
docker-compose build frontend
docker-compose up -d frontend
```

### 2. Не підключається до API

**Перевірка:**
```bash
# Перевірка env змінних
docker exec expense-tracker-frontend env | grep REACT_APP

# Має бути:
REACT_APP_API_URL=http://localhost:8080/api
```

**Якщо неправильно:**
1. Оновіть `frontend/.env`
2. Перебудуйте: `docker-compose build frontend`
3. Перезапустіть: `docker-compose up -d frontend`

## Проблема: Дані не зберігаються

### База даних скидується

**Причина:**
Volume не створений або видалений

**Перевірка:**
```bash
docker volume ls | grep postgres
```

**Рішення:**
```bash
# НЕ робіть docker-compose down -v (це видалить дані)
# Використовуйте просто:
docker-compose down
docker-compose up -d
```

## Проблема: Повільна робота

### 1. Недостатньо пам'яті

**Перевірка:**
```bash
docker stats
```

**Рішення:**
- Збільшіть RAM для Docker Desktop
- Додайте memory limits в docker-compose.yml:
```yaml
services:
  backend:
    mem_limit: 1g
```

### 2. Slow build times

**Рішення:**
```bash
# Використовуйте кеш
docker-compose build

# Якщо потрібна чиста збірка
docker-compose build --no-cache
```

## Перевірка всієї системи

### Комплексна діагностика

**Скрипт перевірки:**
```bash
#!/bin/bash

echo "=== Checking Docker ==="
docker --version
docker-compose --version

echo ""
echo "=== Checking containers ==="
docker-compose ps

echo ""
echo "=== Checking backend health ==="
curl -f http://localhost:8080/actuator/health || echo "Backend not responding"

echo ""
echo "=== Checking database ==="
docker exec expense-tracker-db pg_isready -U postgres || echo "Database not ready"

echo ""
echo "=== Checking frontend ==="
curl -f http://localhost:3000 > /dev/null || echo "Frontend not responding"

echo ""
echo "=== Recent backend logs ==="
docker logs --tail 20 expense-tracker-backend

echo ""
echo "=== PostgreSQL status ==="
docker exec expense-tracker-db psql -U postgres -d expense_tracker_db -c "\dt"
```

Збережіть як `check-system.sh` та запустіть:
```bash
chmod +x check-system.sh
./check-system.sh
```

## Повне перевстановлення

**Якщо нічого не допомагає:**

```bash
# 1. Зупинити все
docker-compose down

# 2. Видалити образи (але НЕ volumes якщо хочете зберегти дані)
docker rmi expense-tracker-backend expense-tracker-frontend

# 3. Очистити build cache
docker builder prune

# 4. Перебудувати все
docker-compose build --no-cache

# 5. Запустити
docker-compose up -d

# 6. Подивитися логи
docker-compose logs -f
```

## Типові помилки в логах

### "Connection refused: postgres:5432"
**Рішення:** PostgreSQL ще не встав. Зачекайте 30 секунд та перезапустіть backend.

### "Port 8080 is already in use"
**Рішення:** Інший процес використовує порт. Знайдіть та зупиніть його.

### "No such table"
**Рішення:**
- Перевірте `spring.jpa.hibernate.ddl-auto=update` в application.properties
- Або вручну створіть таблиці

### "Cannot build image"
**Рішення:**
- Перевірте Dockerfile
- Перевірте наявність інтернету для завантаження залежностей
- Спробуйте `docker-compose build --no-cache`

## Корисні команди

```bash
# Показати всі логи
docker-compose logs

# Логи конкретного сервісу
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres

# Follow logs (real-time)
docker-compose logs -f backend

# Перезапуск сервісу
docker-compose restart backend

# Зупинка
docker-compose stop

# Повний перезапуск
docker-compose down
docker-compose up -d

# Доступ до контейнера
docker exec -it expense-tracker-backend sh
docker exec -it expense-tracker-db psql -U postgres -d expense_tracker_db

# Перевірка мережі
docker network inspect smart-expense-tracker_expense-tracker-network

# Очистка невикористаних ресурсів
docker system prune
```

## Контакти для підтримки

Якщо проблема не вирішена:
1. Перевірте Issues на GitHub
2. Створіть новий Issue з описом проблеми та логами
3. Додайте вивід `docker-compose logs`

## Успішний запуск

Коли все працює правильно, ви побачите:

```bash
$ docker-compose ps
NAME                          STATUS              PORTS
expense-tracker-backend       Up (healthy)        0.0.0.0:8080->8080/tcp
expense-tracker-db            Up (healthy)        0.0.0.0:5432->5432/tcp
expense-tracker-frontend      Up                  0.0.0.0:3000->3000/tcp
```

**Доступні URLs:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health
