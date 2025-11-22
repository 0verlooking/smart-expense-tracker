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

#### 3.1 Управління користувачами
- Реєстрація нових користувачів
- Авторизація в системі
- Редагування профілю користувача
- Видалення облікового запису

#### 3.2 Управління категоріями витрат
- Створення нових категорій
- Редагування існуючих категорій
- Видалення категорій
- Призначення кольорів та іконок категоріям

#### 3.3 Управління витратами
- Додавання нових записів витрат
- Редагування існуючих витрат
- Видалення витрат
- Пошук витрат за різними критеріями:
  - За датою
  - За категорією
  - За сумою
  - За способом оплати

#### 3.4 Управління бюджетом
- Створення бюджетних планів
- Встановлення лімітів на різні періоди (тиждень, місяць, рік)
- Відстеження виконання бюджету
- Сповіщення про перевищення бюджету

#### 3.5 Аналітика та звітність
- Візуалізація витрат за категоріями (кругова діаграма)
- Графік витрат за періодами (стовпчаста діаграма)
- Підрахунок загальних витрат
- Порівняння бюджету та фактичних витрат

### 4. Технічні вимоги

#### 4.1 Backend
- **Мова програмування:** Java 17
- **Фреймворк:** Spring Boot 3.2.0
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
4. **Dependency Injection** - для управління залежностями
5. **Factory Pattern** - для створення об'єктів
6. **Strategy Pattern** - для різних стратегій обробки

#### 5.3 Database Schema

**Таблиці:**

1. **users**
   - id (PK)
   - username
   - email
   - password
   - full_name
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

#### 6.1 Users API
- GET /api/users - отримати всіх користувачів
- GET /api/users/{id} - отримати користувача за ID
- POST /api/users - створити користувача
- PUT /api/users/{id} - оновити користувача
- DELETE /api/users/{id} - видалити користувача

#### 6.2 Categories API
- GET /api/categories/user/{userId} - отримати категорії користувача
- GET /api/categories/{id} - отримати категорію за ID
- POST /api/categories - створити категорію
- PUT /api/categories/{id} - оновити категорію
- DELETE /api/categories/{id} - видалити категорію

#### 6.3 Expenses API
- GET /api/expenses/user/{userId} - отримати витрати користувача
- GET /api/expenses/{id} - отримати витрату за ID
- GET /api/expenses/user/{userId}/daterange - витрати за період
- POST /api/expenses - створити витрату
- PUT /api/expenses/{id} - оновити витрату
- DELETE /api/expenses/{id} - видалити витрату

#### 6.4 Budgets API
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
- Валідація вхідних даних
- Обробка помилок
- SQL injection protection (через JPA)
- XSS protection

#### 7.3 Масштабованість
- Горизонтальне масштабування через Docker
- Незалежне масштабування backend та frontend

#### 7.4 Надійність
- Централізована обробка помилок
- Логування операцій
- Health checks для сервісів

### 8. Інтерфейс користувача

#### 8.1 Основні екрани
1. **Dashboard** - головна сторінка з аналітикою
2. **Expenses** - список витрат
3. **Categories** - управління категоріями
4. **Budgets** - управління бюджетами

#### 8.2 UI Компоненти
- Navigation Drawer - бокова навігація
- Data Tables - таблиці даних
- Forms - форми введення
- Charts - графіки та діаграми
- Dialogs - модальні вікна

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
- Sequence діаграми
- ER діаграма бази даних
- Wireframes інтерфейсу

#### 11.2 Технічна документація
- API документація (Swagger)
- README з інструкціями запуску
- Опис архітектури

### 12. Підсумок

Система Smart Expense Tracker є повнофункціональним веб-додатком для управління особистими фінансами з сучасною архітектурою, що відповідає принципам SOLID та використовує кращі практики розробки програмного забезпечення.
