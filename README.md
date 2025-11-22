# Smart Expense Tracker

Система трекінгу витрат - повнофункціональний веб-додаток для управління особистими фінансами.

## Опис проекту

Smart Expense Tracker - це сучасна система для відстеження та аналізу особистих витрат. Проект розроблений з використанням найкращих практик розробки програмного забезпечення, включаючи принципи SOLID та патерни проектування.

### Основні можливості

- **Автентифікація та авторизація** - JWT-based authentication з роль-based доступом
- **Управління користувачами** - Admin панель для керування користувачами
- **Управління витратами** - створення, редагування, видалення витрат
- **Категоризація витрат** - гнучка система категорій
- **Управління бюджетами** - планування та контроль витрат
- **Аналітика та візуалізація** - графіки та звіти
- **Фільтрація витрат** - пошук за різними критеріями
- **Респонсивний дизайн** - адаптивний інтерфейс

## Технологічний стек

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- PostgreSQL 15
- Hibernate (JPA)
- Maven
- Swagger/OpenAPI
- BCrypt (password hashing)

### Frontend
- React 18
- Material-UI 5
- Recharts
- Axios
- React Router v6

### DevOps
- Docker
- Docker Compose
- Nginx

## Архітектура

### Backend Architecture

Проект використовує багатошарову архітектуру:

```
┌─────────────────────────┐
│   Presentation Layer    │  ← REST Controllers
├─────────────────────────┤
│ Business Logic Layer    │  ← Services (SOLID)
├─────────────────────────┤
│  Data Access Layer      │  ← Repositories
├─────────────────────────┤
│    Database Layer       │  ← PostgreSQL
└─────────────────────────┘
```

### Принципи SOLID

1. **Single Responsibility** - кожен клас має одну відповідальність
2. **Open/Closed** - відкритий для розширення, закритий для модифікації
3. **Liskov Substitution** - можливість заміни батьківського класу
4. **Interface Segregation** - розділення інтерфейсів
5. **Dependency Inversion** - залежність від абстракцій

### Патерни проектування

- **Repository Pattern** - для роботи з даними
- **Service Layer Pattern** - для бізнес-логіки
- **DTO Pattern** - для передачі даних
- **Dependency Injection** - управління залежностями
- **Factory Pattern** - створення об'єктів
- **Strategy Pattern** - різні стратегії обробки

## Структура проекту

```
smart-expense-tracker/
├── backend/                    # Backend (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/expensetracker/
│   │   │   │   ├── model/          # Entity класи
│   │   │   │   ├── repository/     # Repository інтерфейси
│   │   │   │   ├── service/        # Service класи
│   │   │   │   ├── controller/     # REST Controllers
│   │   │   │   ├── dto/            # DTO класи
│   │   │   │   ├── config/         # Конфігурація
│   │   │   │   └── exception/      # Обробка помилок
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                   # Frontend (React)
│   ├── public/
│   ├── src/
│   │   ├── components/         # React компоненти
│   │   ├── services/           # API сервіси
│   │   ├── pages/              # Сторінки
│   │   └── utils/              # Утиліти
│   ├── package.json
│   ├── Dockerfile
│   └── nginx.conf
├── docs/                       # Документація
│   ├── diagrams/               # UML діаграми
│   ├── wireframes/             # Wireframes
│   └── technical-specification.md
├── docker-compose.yml
└── README.md
```

## Встановлення та запуск

### Передумови

- Docker
- Docker Compose

### Запуск через Docker Compose (Рекомендовано)

1. Клонуйте репозиторій:
```bash
git clone https://github.com/yourusername/smart-expense-tracker.git
cd smart-expense-tracker
```

2. Запустіть всі сервіси одною командою:
```bash
docker-compose up --build
```

3. Дочекайтесь завершення збірки та запуску всіх контейнерів

4. Відкрийте додаток у браузері:
   - **Frontend:** http://localhost:3000
   - **Backend API:** http://localhost:8080
   - **Swagger UI:** http://localhost:8080/swagger-ui.html

5. Увійдіть в систему, використовуючи тестовий акаунт:
   - **Admin:** username=`admin`, password=`admin123`
   - **User:** username=`user`, password=`user123`

> **Примітка:** Детальна інформація про аутентифікацію доступна в [AUTHENTICATION.md](./AUTHENTICATION.md)

### Запуск без Docker

#### Backend

1. Переконайтесь, що PostgreSQL запущена
2. Створіть базу даних `expense_tracker_db`
3. Перейдіть у директорію backend:
```bash
cd backend
```

4. Запустіть додаток:
```bash
./mvnw spring-boot:run
```

#### Frontend

1. Перейдіть у директорію frontend:
```bash
cd frontend
```

2. Встановіть залежності:
```bash
npm install
```

3. Запустіть додаток:
```bash
npm start
```

## API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - реєстрація нового користувача
- `POST /api/auth/login` - вхід в систему (отримання JWT токену)

### Admin (Admin Only)
- `GET /api/admin/users` - отримати всіх користувачів
- `PUT /api/admin/users/{id}/role` - змінити роль користувача
- `DELETE /api/admin/users/{id}` - видалити користувача

### Users (Protected)
- `GET /api/users` - отримати всіх користувачів
- `GET /api/users/{id}` - отримати користувача за ID
- `POST /api/users` - створити користувача
- `PUT /api/users/{id}` - оновити користувача
- `DELETE /api/users/{id}` - видалити користувача

### Expenses
- `GET /api/expenses/user/{userId}` - отримати витрати користувача
- `GET /api/expenses/{id}` - отримати витрату за ID
- `POST /api/expenses` - створити витрату
- `PUT /api/expenses/{id}` - оновити витрату
- `DELETE /api/expenses/{id}` - видалити витрату

### Categories
- `GET /api/categories/user/{userId}` - отримати категорії користувача
- `POST /api/categories` - створити категорію
- `PUT /api/categories/{id}` - оновити категорію
- `DELETE /api/categories/{id}` - видалити категорію

### Budgets
- `GET /api/budgets/user/{userId}` - отримати бюджети користувача
- `GET /api/budgets/user/{userId}/active` - активні бюджети
- `POST /api/budgets` - створити бюджет
- `PUT /api/budgets/{id}` - оновити бюджет
- `DELETE /api/budgets/{id}` - видалити бюджет

## База даних

### ER Діаграма

Проект використовує PostgreSQL з наступними таблицями:

- **users** - користувачі системи
- **categories** - категорії витрат
- **expenses** - записи витрат
- **budgets** - бюджетні плани

Детальна ER діаграма доступна в `docs/diagrams/er-diagram.puml`

## Документація

- **Authentication Guide:** [AUTHENTICATION.md](./AUTHENTICATION.md) - Повний гайд по автентифікації та авторизації
- **Технічне завдання:** `docs/technical-specification.md`
- **Use Case діаграма:** `docs/diagrams/use-case-diagram.puml`
- **Sequence діаграми:** `docs/diagrams/`
- **ER діаграма:** `docs/diagrams/er-diagram.puml`
- **Wireframes:** `docs/wireframes/`
- **Troubleshooting:** [TROUBLESHOOTING.md](./TROUBLESHOOTING.md)
- **Quick Start:** [QUICK_START.md](./QUICK_START.md)

## Тестування

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## Docker конфігурація

Проект використовує multi-stage builds для оптимізації розміру образів:

- **Backend:** Maven build + JRE runtime
- **Frontend:** Node build + Nginx runtime
- **Database:** PostgreSQL 15 Alpine

### Сервіси Docker Compose

1. **postgres** - База даних (порт 5432)
2. **backend** - Spring Boot API (порт 8080)
3. **frontend** - React додаток (порт 3000)

## Розгортання

### Production

Для production розгортання:

```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Корисні команди

### Перегляд логів
```bash
docker-compose logs -f
```

### Зупинка сервісів
```bash
docker-compose down
```

### Очистка volumes
```bash
docker-compose down -v
```

### Перебудова образів
```bash
docker-compose build --no-cache
```

## Troubleshooting

### Backend не підключається до БД

Переконайтесь, що PostgreSQL контейнер запущений:
```bash
docker-compose ps
```

### Frontend не може підключитися до Backend

Перевірте, чи Backend запущений:
```bash
curl http://localhost:8080/actuator/health
```

## Реалізовані можливості

✅ **Автентифікація та авторизація** - JWT-based authentication з ролями USER/ADMIN
✅ **Управління користувачами** - Admin панель для керування користувачами та ролями
✅ **Управління витратами** - CRUD операції з витратами
✅ **Категоризація** - Власні категорії для кожного користувача
✅ **Бюджети** - Створення та контроль бюджетів
✅ **Аналітика** - Візуалізація витрат та статистика
✅ **Docker deployment** - Повна контейнеризація додатку

## Майбутні покращення

- Refresh токени для автоматичного продовження сесії
- Password reset через email
- Email верифікація при реєстрації
- 2FA (Two-Factor Authentication)
- OAuth2 інтеграція (Google, GitHub)
- Експорт звітів у PDF/Excel
- Мобільний додаток (React Native)
- Push-сповіщення про перевищення бюджету
- Інтеграція з банківськими API
- Багатовалютність
- Розширена аналітика з ML прогнозами

## Автор

Курсова робота з розробки веб-систем

## Ліцензія

MIT License
