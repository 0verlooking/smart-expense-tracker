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

**ВАЖЛИВО: Якщо це перший запуск або були проблеми з базою даних:**

```bash
# Використайте скрипт restart (рекомендовано)
./restart.sh
```

**АБО вручну:**

```bash
# 1. Перейдіть у директорію проекту
cd smart-expense-tracker

# 2. Зупиніть старі контейнери та видаліть volumes
docker-compose down -v

# 3. Запустіть всі сервіси
docker-compose up --build
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

### Крок 4: Відкрийте додаток та увійдіть

**Frontend (Інтерфейс користувача):**
🌐 http://localhost:3000

Ви побачите сторінку входу. **Використайте тестовий акаунт:**

**👤 Акаунт адміністратора:**
- Username: `admin`
- Password: `admin123`
- Можливості: управління користувачами + всі функції

**👤 Акаунт користувача:**
- Username: `user`
- Password: `user123`
- Можливості: управління витратами, категоріями, бюджетами

**Backend API (Swagger документація):**
📚 http://localhost:8080/swagger-ui.html

**Health Check:**
💚 http://localhost:8080/actuator/health

## Перші кроки в додатку

### 0. Вхід в систему
1. Відкрийте http://localhost:3000
2. Введіть credentials: **admin** / **admin123** (або user/user123)
3. Натисніть **Login**

### 1. Dashboard
Після входу ви побачите:
- 8 тестових витрат користувача "user"
- 6 категорій
- 2 бюджети
- Графіки та аналітику

**Якщо ви адмін:** у меню буде пункт "Admin Panel" для управління користувачами

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

### ❌ Помилка: "column role of relation users contains null values"

**Це означає що стара база даних без автентифікації заважає.**

**РІШЕННЯ:**
```bash
# Використайте скрипт restart (РЕКОМЕНДОВАНО!)
./restart.sh

# АБО вручну:
docker-compose down -v
docker-compose up --build
```

### Backend не відповідає (404 або 500 помилки)

**Швидке рішення:**
```bash
# Використайте restart script
./restart.sh

# АБО перезапустіть backend
docker-compose restart backend

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
# 🔄 ПОВНИЙ ПЕРЕЗАПУСК (видаляє стару базу)
./restart.sh

# Зупинити всі сервіси
docker-compose stop

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

**Користувачі:**
- **Admin:** username=`admin`, password=`admin123`, role=ADMIN
- **User:** username=`user`, password=`user123`, role=USER

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
- 🔐 Authentication Guide: `AUTHENTICATION.md`
- 🔧 Troubleshooting: `TROUBLESHOOTING.md`
- 📚 API Documentation: http://localhost:8080/swagger-ui.html

---

**Готово! Насолоджуйтесь використанням Smart Expense Tracker! 🎉**
