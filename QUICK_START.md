# Quick Start Guide
## Smart Expense Tracker

## Швидкий запуск за 3 хвилини

### Крок 1: Перевірка передумов

```bash
# Перевірте чи встановлений Docker
docker --version
# Має показати: Docker version 20.10+

# Перевірте Docker Compose
docker compose version
# Має показати: Docker Compose version 2.0+
```

**Якщо Docker не встановлений:**
- Windows/Mac: Встановіть [Docker Desktop](https://www.docker.com/products/docker-desktop)
- Linux: `sudo apt-get install docker.io docker-compose-plugin`

### Крок 2: Запуск проекту

```bash
# 1. Перейдіть у директорію проекту
cd smart-expense-tracker

# 2. Запустіть всі сервіси
docker compose up -d --build
```

**Це запустить:**
- ✅ PostgreSQL база даних (порт 5432)
- ✅ Spring Boot backend (порт 8080)
- ✅ React frontend (порт 3000)

### Крок 3: Дочекайтеся запуску (30-60 секунд)

```bash
# Подивитися процес запуску
docker compose logs -f

# Або запустіть скрипт перевірки
chmod +x check-system.sh
./check-system.sh
```

**Індикатори успішного запуску:**
```
✓ PostgreSQL is ready
✓ Backend is healthy
✓ Frontend is accessible
```

### Крок 4: Відкрийте додаток

**Frontend (Інтерфейс користувача):**
🌐 http://localhost:3000

**Backend API (Swagger документація):**
📚 http://localhost:8080/swagger-ui.html

**Health Check:**
💚 http://localhost:8080/actuator/health

## Перші кроки в додатку

### 1. Dashboard
При першому запуску ви побачите:
- 8 тестових витрат
- 6 категорій
- 2 бюджети
- Графіки та аналітику

### 2. Додавання нової витрати
1. Натисніть кнопку **"+"** (справа внизу)
2. Заповніть форму:
   - Amount: 50.00
   - Date: сьогодні
   - Category: Food & Dining
   - Payment Method: Credit Card
   - Description: Coffee and breakfast
3. Натисніть **"Add"**

### 3. Управління категоріями
1. Перейдіть у меню **"Categories"**
2. Натисніть **"Add Category"**
3. Створіть свою категорію з кольором та іконкою

### 4. Налаштування бюджету
1. Додайте власний бюджет
2. Відстежуйте витрати в реальному часі

## Що якщо щось не працює?

### Backend не відповідає (404 помилки)

**Швидке рішення:**
```bash
# Перезапустіть backend
docker compose restart backend

# Дочекайтеся 30 секунд
# Перевірте: http://localhost:8080/actuator/health
```

**Детальна діагностика:**
```bash
# Подивіться логи backend
docker compose logs backend

# Шукайте помилки типу:
# - "Connection refused" → PostgreSQL не готова
# - "Port in use" → Порт зайнятий
# - "Cannot find table" → БД не створилася
```

### Frontend показує помилки

**Рішення:**
```bash
# Перебудуйте frontend
docker compose build frontend
docker compose up -d frontend

# Очистіть кеш браузера (Ctrl+Shift+R)
```

### База даних порожня

**Якщо не з'явилися тестові дані:**
```bash
# Перевірте чи data.sql виконався
docker compose logs backend | grep "data.sql"

# Якщо ні - виконайте вручну:
docker exec -i expense-tracker-db psql -U postgres -d expense_tracker_db < backend/src/main/resources/data.sql
```

## Корисні команди

```bash
# Зупинити всі сервіси
docker compose stop

# Запустити знову
docker compose start

# Перезапустити конкретний сервіс
docker compose restart backend

# Подивитися статус
docker compose ps

# Подивитися логи
docker compose logs -f backend

# Зупинити та видалити контейнери (БЕЗ видалення даних)
docker compose down

# Повне очищення (ВИДАЛИТЬ ВСІ ДАНІ!)
docker compose down -v
```

## Тестові дані

Система автоматично створює:

**Користувач:**
- Username: `testuser`
- Email: `test@example.com`

**Категорії (6):**
- 🍔 Food & Dining
- 🚗 Transportation
- 🛍️ Shopping
- 💊 Healthcare
- 💡 Utilities
- 🎬 Entertainment

**Витрати (8):**
- Різні витрати за останні 7 днів
- Загальна сума: ~₴384.44

**Бюджети (2):**
- Monthly Budget: ₴2000/місяць
- Weekly Budget: ₴500/тиждень

## Наступні кроки

1. ✅ Додайте свої реальні витрати
2. ✅ Створіть власні категорії
3. ✅ Налаштуйте бюджети
4. ✅ Аналізуйте свої витрати через Dashboard
5. ✅ Експлоруйте API через Swagger

## Зупинка проекту

```bash
# Коли закінчили роботу:
docker compose stop

# Або зупинити і видалити контейнери (дані збережуться):
docker compose down
```

## Потрібна допомога?

- 📖 Детальна документація: `README.md`
- 🔧 Troubleshooting: `TROUBLESHOOTING.md`
- 🏗️ Архітектура: `docs/architecture.md`
- 🚀 Deployment: `docs/deployment-guide.md`

---

**Готово! Насолоджуйтесь використанням Smart Expense Tracker! 🎉**
