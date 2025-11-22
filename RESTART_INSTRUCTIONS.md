# 🚨 ВАЖЛИВО: ІНСТРУКЦІЇ ПО ПЕРЕЗАПУСКУ

## Проблема

Ви бачите помилку **500 Internal Server Error** при логіні тому що:
- ✅ Новий код з автентифікацією запушений на Git
- ✅ Файли на вашому комп'ютері оновлені
- ❌ **Але Docker контейнери запущені зі СТАРИМ кодом!**

## Рішення

### ВАРІАНТ 1: Використайте скрипт (НАЙПРОСТІШЕ!)

```bash
./restart.sh
```

### ВАРІАНТ 2: Вручну

```bash
# 1. ЗУПИНІТЬ контейнери та ВИДАЛІТЬ volumes (стара база даних)
docker-compose down -v

# 2. ПЕРЕБУДУЙТЕ образи та ЗАПУСТІТЬ
docker-compose up --build
```

### ВАРІАНТ 3: Пошагово з перевіркою

```bash
# Крок 1: Зупиніть все
docker-compose down

# Крок 2: Видаліть стару базу даних
docker volume rm smart-expense-tracker_postgres_data

# Крок 3: Видаліть старі образи (опціонально але рекомендовано)
docker-compose build --no-cache

# Крок 4: Запустіть з новим кодом
docker-compose up
```

## Що станеться після перезапуску?

1. ✅ Docker **ПЕРЕБУДУЄ** backend з вашим новим кодом
2. ✅ Створить **НОВУ** базу даних з полем `role`
3. ✅ Запустить **data.sql** який створить таблиці та додасть користувачів
4. ✅ Backend успішно запуститься
5. ✅ Логін запрацює!

## Перевірка після перезапуску

```bash
# Перевірте що всі контейнери запущені
docker-compose ps

# Перевірте що backend здоровий
curl http://localhost:8080/actuator/health

# Очікувана відповідь:
# {"status":"UP"}
```

## Увійдіть в систему

Відкрийте http://localhost:3000

**Тестові акаунти:**
- Admin: username=`admin`, password=`admin123`
- User: username=`user`, password=`user123`

## Якщо все ще не працює

1. Подивіться логи backend:
```bash
docker-compose logs backend
```

2. Шукайте помилки в логах

3. Якщо бачите "column role does not exist" - значить база не пересоздалась. Виконайте:
```bash
docker volume rm smart-expense-tracker_postgres_data
docker-compose up --build
```

## Чому це важливо?

Docker **НЕ** оновлює код автоматично. Коли ви:
- Змінюєте файли коду
- Змінюєте data.sql
- Змінюєте Dockerfile

Ви **ПОВИННІ** перебудувати Docker образи командою:
```bash
docker-compose up --build
```

## Підсумок команд

```bash
# ⚡ ШВИДКИЙ ПЕРЕЗАПУСК (рекомендовано)
./restart.sh

# 🔄 АБО вручну
docker-compose down -v && docker-compose up --build

# 🐛 Якщо є проблеми - повна очистка
docker-compose down -v
docker volume prune -f
docker-compose build --no-cache
docker-compose up
```

---

**ВИКОНАЙТЕ ЦІ КОМАНДИ ЗАРАЗ і система запрацює!** 🚀
