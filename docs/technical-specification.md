# Технічне завдання
## Система трекінгу витрат (Smart Expense Tracker)

### 1. Загальна інформація

**Назва проекту:** Smart Expense Tracker
**Версія:** 1.0.0
**Дата:** 2025

### 2. Мета та призначення системи

Система призначена для автоматизації обліку особистих фінансових витрат користувачів. Основна мета - надати зручний інструмент для:
- Відстеження щоденних витрат
- Категоризації витрат
- Аналізу фінансових потоків
- Планування бюджету
- Візуалізації статистики витрат

### 3. Функціональні вимоги

#### 3.1 Автентифікація та авторизація
- **Реєстрація нових користувачів** з автоматичним присвоєнням ролі USER
- **JWT-based авторизація** для безпечного доступу до API
- **Роль-based доступ (RBAC):**
  - **USER** - доступ до власних витрат, категорій та бюджетів
  - **ADMIN** - повний доступ + управління користувачами
- **BCrypt хешування паролів** (strength 10)
- **Захищені маршрути** на frontend та backend
- **Автоматичний logout** після закінчення терміну дії токену (24 години)

#### 3.2 Управління користувачами
- Реєстрація нових користувачів
- Авторизація в системі
- Редагування профілю користувача
- Видалення облікового запису
- **Admin функції:**
  - Перегляд всіх користувачів системи
  - Зміна ролей користувачів (USER ↔ ADMIN)
  - Видалення користувачів

#### 3.3 Управління категоріями витрат
- Створення нових категорій
- Редагування існуючих категорій
- Видалення категорій
- Призначення кольорів та іконок категоріям

#### 3.4 Управління витратами
- Додавання нових записів витрат
- Редагування існуючих витрат
- Видалення витрат
- Пошук витрат за різними критеріями:
  - За датою
  - За категорією
  - За сумою
  - За способом оплати

#### 3.5 Управління бюджетом
- Створення бюджетних планів
- Встановлення лімітів на різні періоди (тиждень, місяць, рік)
- Відстеження виконання бюджету
- Сповіщення про перевищення бюджету

#### 3.6 Аналітика та звітність
- Візуалізація витрат за категоріями (кругова діаграма)
- Графік витрат за періодами (стовпчаста діаграма)
- Підрахунок загальних витрат
- Порівняння бюджету та фактичних витрат

### 4. Технічні вимоги

#### 4.1 Backend
- **Мова програмування:** Java 17
- **Фреймворк:** Spring Boot 3.2.0
- **Безпека:** Spring Security + JWT (JSON Web Tokens)
- **Хешування паролів:** BCrypt (strength 10)
- **База даних:** PostgreSQL 15
- **ORM:** Hibernate (JPA)
- **Build tool:** Maven
- **API:** RESTful API
- **Документація API:** Swagger/OpenAPI

#### 4.2 Frontend
- **Бібліотека:** React 18
- **UI Framework:** Material-UI (MUI) 5
- **Графіки:** Recharts
- **HTTP Client:** Axios
- **Роутинг:** React Router v6

#### 4.3 Розгортання
- **Контейнеризація:** Docker
- **Оркестрація:** Docker Compose
- **Web Server (Frontend):** Nginx

### 5. Архітектурні рішення

#### 5.1 Backend Architecture
**Багатошарова архітектура:**
- **Presentation Layer:** REST Controllers
- **Business Logic Layer:** Services
- **Data Access Layer:** Repositories
- **Database Layer:** PostgreSQL

**Застосовані принципи SOLID:**
- **Single Responsibility:** Кожен клас має одну відповідальність
- **Open/Closed:** Класи відкриті для розширення, закриті для модифікації
- **Liskov Substitution:** Можливість заміни батьківського класу на дочірній
- **Interface Segregation:** Розділення інтерфейсів на специфічні
- **Dependency Inversion:** Залежність від абстракцій, а не від конкретних реалізацій

#### 5.2 Патерни проектування
1. **Repository Pattern** - для роботи з даними
2. **Service Layer Pattern** - для бізнес-логіки
3. **DTO Pattern** - для передачі даних між шарами
4. **Dependency Injection** - для управління залежностями (Spring IoC Container)
5. **Factory Pattern** - для створення об'єктів
6. **Strategy Pattern** - для різних стратегій обробки
7. **Filter/Chain of Responsibility Pattern** - JWT Authentication Filter
8. **Builder Pattern** - для створення складних об'єктів (AuthResponse, User)
9. **Facade Pattern** - AuthService як фасад для автентифікації

#### 5.3 Database Schema

**Таблиці:**

1. **users**
   - id (PK)
   - username (UNIQUE, NOT NULL)
   - email (UNIQUE, NOT NULL)
   - password (NOT NULL, BCrypt hashed)
   - full_name
   - role (NOT NULL, ENUM: USER, ADMIN, default: USER)
   - created_at
   - updated_at

2. **categories**
   - id (PK)
   - name
   - description
   - icon_name
   - color_code
   - user_id (FK)
   - created_at

3. **expenses**
   - id (PK)
   - amount
   - date
   - description
   - merchant
   - payment_method
   - notes
   - user_id (FK)
   - category_id (FK)
   - created_at
   - updated_at

4. **budgets**
   - id (PK)
   - amount
   - start_date
   - end_date
   - budget_name
   - period_type
   - user_id (FK)
   - created_at
   - updated_at

### 6. API Endpoints

#### 6.1 Authentication API (Public)
- POST /api/auth/register - реєстрація нового користувача
  - Request: { username, email, password, fullName }
  - Response: { token, userId, username, email, fullName, role }
- POST /api/auth/login - авторизація користувача
  - Request: { username, password }
  - Response: { token, userId, username, email, fullName, role }

#### 6.2 Admin API (Admin Only - requires ADMIN role)
- GET /api/admin/users - отримати всіх користувачів системи
- PUT /api/admin/users/{id}/role - змінити роль користувача
  - Request: { role: "USER" | "ADMIN" }
- DELETE /api/admin/users/{id} - видалити користувача

#### 6.3 Users API (Protected - requires authentication)
- GET /api/users - отримати всіх користувачів
- GET /api/users/{id} - отримати користувача за ID
- POST /api/users - створити користувача
- PUT /api/users/{id} - оновити користувача
- DELETE /api/users/{id} - видалити користувача

#### 6.4 Categories API (Protected)
- GET /api/categories/user/{userId} - отримати категорії користувача
- GET /api/categories/{id} - отримати категорію за ID
- POST /api/categories - створити категорію
- PUT /api/categories/{id} - оновити категорію
- DELETE /api/categories/{id} - видалити категорію

#### 6.5 Expenses API (Protected)
- GET /api/expenses/user/{userId} - отримати витрати користувача
- GET /api/expenses/{id} - отримати витрату за ID
- GET /api/expenses/user/{userId}/daterange - витрати за період
- POST /api/expenses - створити витрату
- PUT /api/expenses/{id} - оновити витрату
- DELETE /api/expenses/{id} - видалити витрату

#### 6.6 Budgets API (Protected)
- GET /api/budgets/user/{userId} - отримати бюджети користувача
- GET /api/budgets/{id} - отримати бюджет за ID
- GET /api/budgets/user/{userId}/active - активні бюджети
- POST /api/budgets - створити бюджет
- PUT /api/budgets/{id} - оновити бюджет
- DELETE /api/budgets/{id} - видалити бюджет

### 7. Нефункціональні вимоги

#### 7.1 Продуктивність
- Час відгуку API: < 500ms для 95% запитів
- Підтримка одночасних користувачів: до 1000

#### 7.2 Безпека
- **JWT Authentication** - stateless authentication з токенами
- **BCrypt Password Hashing** - криптографічне хешування паролів (strength 10)
- **Role-Based Access Control (RBAC)** - контроль доступу на основі ролей
- **Spring Security** - комплексний security framework
- **CORS Configuration** - налаштування для frontend-backend взаємодії
- **Валідація вхідних даних** - Bean Validation (JSR-303)
- **Обробка помилок** - централізована обробка з правильними HTTP статусами
- **SQL injection protection** - через JPA Criteria API та параметризовані запити
- **XSS protection** - валідація та санітизація вхідних даних
- **HTTPS ready** - готовність до використання HTTPS в production

#### 7.3 Масштабованість
- Горизонтальне масштабування через Docker
- Незалежне масштабування backend та frontend

#### 7.4 Надійність
- Централізована обробка помилок
- Логування операцій
- Health checks для сервісів

### 8. Інтерфейс користувача

#### 8.1 Основні екрани
1. **Login** - сторінка авторизації
2. **Register** - сторінка реєстрації
3. **Dashboard** - головна сторінка з аналітикою
4. **Expenses** - список витрат з можливістю додавання/редагування
5. **Categories** - управління категоріями
6. **Budgets** - управління бюджетами
7. **Admin Panel** - панель адміністратора (тільки для ADMIN ролі)
   - Перегляд всіх користувачів
   - Зміна ролей користувачів
   - Видалення користувачів

#### 8.2 UI Компоненти
- **Login Form** - форма авторизації з валідацією
- **Register Form** - форма реєстрації з валідацією полів
- **Protected Route** - HOC для захисту маршрутів
- **Navigation Drawer** - бокова навігація з умовним відображенням Admin Panel
- **Data Tables** - таблиці даних з пагінацією та сортуванням
- **Forms** - форми введення з валідацією
- **Charts** - графіки та діаграми (Recharts)
- **Dialogs** - модальні вікна для підтвердження дій
- **Snackbars** - сповіщення про успішні/помилкові операції
- **User Menu** - меню користувача з logout

### 9. Процес розгортання

#### 9.1 Development
```bash
docker-compose up --build
```

#### 9.2 Production
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### 10. Тестування

#### 10.1 Backend Testing
- Unit Tests для сервісів
- Integration Tests для контролерів
- Repository Tests

#### 10.2 Frontend Testing
- Component Tests
- Integration Tests

### 11. Документація

#### 11.1 Діаграми
- Use Case діаграма
- Sequence діаграми (включаючи Authentication Flow)
- ER діаграма бази даних (з полем role)
- Wireframes інтерфейсу (включаючи Login, Register, Admin Panel)

#### 11.2 Технічна документація
- **API документація** - Swagger/OpenAPI автодокументація
- **README.md** - загальна інформація та інструкції запуску
- **AUTHENTICATION.md** - детальний гайд по автентифікації та авторизації
- **Опис архітектури** - docs/architecture.md
- **Deployment Guide** - docs/deployment-guide.md

### 12. Підсумок

Система Smart Expense Tracker є повнофункціональним веб-додатком для управління особистими фінансами з сучасною архітектурою, що відповідає принципам SOLID та використовує кращі практики розробки програмного забезпечення.

**Ключові особливості:**
- ✅ **Безпечна автентифікація** - JWT + BCrypt з роль-based доступом
- ✅ **SOLID принципи** - чиста архітектура з розділенням відповідальностей
- ✅ **Патерни проектування** - Repository, Service Layer, DTO, Dependency Injection, Builder, Facade, Filter/Chain
- ✅ **Повна контейнеризація** - Docker Compose з multi-stage builds
- ✅ **Сучасний tech stack** - Spring Boot 3.2, React 18, PostgreSQL 15
- ✅ **Admin функціонал** - управління користувачами та ролями
- ✅ **Респонсивний UI** - Material-UI з адаптивним дизайном
