# Керівництво з розгортання
## Smart Expense Tracker - Docker Deployment

## Огляд архітектури розгортання

Проект використовує Docker Compose для оркестрації трьох основних сервісів:

```
┌─────────────────────────────────────────────────────┐
│                   Docker Host                        │
│                                                      │
│  ┌────────────────────────────────────────────────┐ │
│  │         expense-tracker-network                 │ │
│  │                                                  │ │
│  │  ┌──────────────┐  ┌──────────────┐            │ │
│  │  │   Frontend   │  │   Backend    │            │ │
│  │  │   (React)    │  │ (Spring Boot)│            │ │
│  │  │   :3000      │←─│   :8080      │            │ │
│  │  └──────────────┘  └───────┬──────┘            │ │
│  │                            │                    │ │
│  │                    ┌───────▼──────┐            │ │
│  │                    │   Database   │            │ │
│  │                    │ (PostgreSQL) │            │ │
│  │                    │    :5432     │            │ │
│  │                    └──────────────┘            │ │
│  └────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────┘
```

## Структура Docker контейнерів

### 1. PostgreSQL Database

**Image:** `postgres:15-alpine`
**Container Name:** `expense-tracker-db`
**Port:** 5432

**Конфігурація:**
```yaml
environment:
  POSTGRES_DB: expense_tracker_db
  POSTGRES_USER: postgres
  POSTGRES_PASSWORD: postgres
```

**Volumes:**
- `postgres_data:/var/lib/postgresql/data` - для персистентності даних

**Health Check:**
```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U postgres"]
  interval: 10s
  timeout: 5s
  retries: 5
```

### 2. Spring Boot Backend

**Base Image:** `eclipse-temurin:17-jre-alpine`
**Container Name:** `expense-tracker-backend`
**Port:** 8080

**Build Process:**

**Stage 1 - Build:**
```dockerfile
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests
```

**Stage 2 - Runtime:**
```dockerfile
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Environment Variables:**
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/expense_tracker_db
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: postgres
```

**Dependencies:**
- Чекає на готовність PostgreSQL через health check

**Health Check:**
```yaml
healthcheck:
  test: ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 60s
```

### 3. React Frontend

**Base Image:** `nginx:alpine`
**Container Name:** `expense-tracker-frontend`
**Port:** 3000

**Build Process:**

**Stage 1 - Build:**
```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
```

**Stage 2 - Runtime:**
```dockerfile
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]
```

**Nginx Configuration:**
```nginx
server {
    listen 3000;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
```

## Процес розгортання

### Крок 1: Підготовка середовища

**Системні вимоги:**
- Docker 20.10+
- Docker Compose 2.0+
- Мінімум 4GB RAM
- 10GB вільного місця на диску

**Перевірка Docker:**
```bash
docker --version
docker-compose --version
```

### Крок 2: Клонування репозиторію

```bash
git clone https://github.com/yourusername/smart-expense-tracker.git
cd smart-expense-tracker
```

### Крок 3: Перевірка конфігурації

**Backend (application.properties):**
```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/expense_tracker_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

**Frontend (.env):**
```
REACT_APP_API_URL=http://localhost:8080/api
```

### Крок 4: Збірка та запуск

**Запуск всіх сервісів:**
```bash
docker-compose up --build
```

**Запуск у фоновому режимі:**
```bash
docker-compose up -d --build
```

### Крок 5: Перевірка статусу

**Перевірка запущених контейнерів:**
```bash
docker-compose ps
```

**Очікуваний вивід:**
```
NAME                          STATUS              PORTS
expense-tracker-backend       Up (healthy)        0.0.0.0:8080->8080/tcp
expense-tracker-db            Up (healthy)        0.0.0.0:5432->5432/tcp
expense-tracker-frontend      Up                  0.0.0.0:3000->3000/tcp
```

**Перевірка логів:**
```bash
# Всі сервіси
docker-compose logs -f

# Конкретний сервіс
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### Крок 6: Перевірка доступності

**Backend Health Check:**
```bash
curl http://localhost:8080/actuator/health
```

**Очікувана відповідь:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

**Frontend:**
Відкрийте браузер: http://localhost:3000

**Swagger UI:**
Відкрийте браузер: http://localhost:8080/swagger-ui.html

## Управління сервісами

### Зупинка сервісів

```bash
# Зупинка всіх сервісів
docker-compose stop

# Зупинка конкретного сервісу
docker-compose stop backend
```

### Перезапуск сервісів

```bash
# Перезапуск всіх сервісів
docker-compose restart

# Перезапуск конкретного сервісу
docker-compose restart backend
```

### Видалення контейнерів

```bash
# Видалення контейнерів (зберігає volumes)
docker-compose down

# Видалення контейнерів та volumes
docker-compose down -v
```

### Перебудова образів

```bash
# Перебудова без кешу
docker-compose build --no-cache

# Перебудова конкретного сервісу
docker-compose build --no-cache backend
```

## Моніторинг та логування

### Перегляд ресурсів

```bash
# Використання ресурсів всіма контейнерами
docker stats
```

### Доступ до контейнера

```bash
# Backend
docker exec -it expense-tracker-backend sh

# Frontend
docker exec -it expense-tracker-frontend sh

# Database
docker exec -it expense-tracker-db psql -U postgres -d expense_tracker_db
```

### Експорт логів

```bash
# Експорт логів backend у файл
docker-compose logs backend > backend-logs.txt

# Експорт всіх логів
docker-compose logs > all-logs.txt
```

## Troubleshooting

### Проблема: Backend не запускається

**Симптоми:**
- Backend контейнер постійно перезапускається
- Помилка підключення до БД

**Рішення:**
```bash
# Перевірка логів
docker-compose logs backend

# Перевірка мережі
docker network inspect smart-expense-tracker_expense-tracker-network

# Перевірка здоров'я PostgreSQL
docker-compose exec postgres pg_isready -U postgres
```

### Проблема: Frontend не може підключитися до Backend

**Симптоми:**
- CORS помилки в браузері
- API запити повертають 502/503

**Рішення:**
```bash
# Перевірка доступності backend
curl http://localhost:8080/actuator/health

# Перевірка nginx конфігурації
docker-compose exec frontend cat /etc/nginx/conf.d/default.conf

# Перезапуск frontend
docker-compose restart frontend
```

### Проблема: База даних втратила дані

**Симптоми:**
- Дані зникли після перезапуску
- Порожня база даних

**Рішення:**
```bash
# Перевірка volumes
docker volume ls | grep postgres

# Відновлення з backup
docker-compose exec postgres psql -U postgres -d expense_tracker_db < backup.sql
```

### Проблема: Недостатньо пам'яті

**Симптоми:**
- Контейнери вмирають (OOMKilled)
- Повільна робота

**Рішення:**
```bash
# Збільшення лімітів в docker-compose.yml
services:
  backend:
    mem_limit: 1g
    mem_reservation: 512m
```

## Backup та Restore

### Backup бази даних

```bash
# Створення backup
docker-compose exec postgres pg_dump -U postgres expense_tracker_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Автоматичний backup (cron)
0 2 * * * cd /path/to/project && docker-compose exec postgres pg_dump -U postgres expense_tracker_db > backup_$(date +\%Y\%m\%d).sql
```

### Restore бази даних

```bash
# Відновлення з backup
docker-compose exec -T postgres psql -U postgres -d expense_tracker_db < backup.sql
```

## Production deployment

### Зміни для production

**docker-compose.prod.yml:**
```yaml
version: '3.8'

services:
  postgres:
    environment:
      POSTGRES_PASSWORD: ${DB_PASSWORD}  # З environment змінних
    restart: always

  backend:
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
    restart: always

  frontend:
    restart: always
```

**Запуск production:**
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Безпека в production

1. **Змінити паролі БД**
2. **Використовувати HTTPS**
3. **Налаштувати firewall**
4. **Обмежити доступ до портів**
5. **Регулярні backup**
6. **Моніторинг логів**

## Масштабування

### Горизонтальне масштабування Backend

```bash
# Запуск кількох інстансів backend
docker-compose up -d --scale backend=3
```

### Load Balancer (Nginx)

**nginx-lb.conf:**
```nginx
upstream backend {
    server backend:8080;
    server backend:8080;
    server backend:8080;
}

server {
    location /api {
        proxy_pass http://backend;
    }
}
```

## Висновок

Розгортання Smart Expense Tracker через Docker Compose забезпечує:

- **Простоту** - один файл конфігурації
- **Відтворюваність** - однакове середовище скрізь
- **Ізоляцію** - сервіси ізольовані в контейнерах
- **Масштабованість** - легко додавати інстанси
- **Портативність** - працює на будь-якій платформі з Docker

Для production рекомендується використовувати Kubernetes або Docker Swarm для кращої оркестрації.
