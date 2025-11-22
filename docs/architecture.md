# Архітектура програмного забезпечення

## Огляд

Smart Expense Tracker побудований за принципами сучасної багатошарової архітектури з чітким розділенням відповідальностей.

## Архітектурні рівні

### 1. Presentation Layer (Рівень представлення)

**Технології:** REST Controllers, Spring Web

**Відповідальність:**
- Обробка HTTP запитів
- Валідація вхідних даних
- Серіалізація/десеріалізація JSON
- Обробка помилок HTTP

**Компоненти:**
- `UserController`
- `ExpenseController`
- `CategoryController`
- `BudgetController`

**Приклад:**
```java
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody ExpenseDTO dto) {
        return new ResponseEntity<>(expenseService.createExpense(dto), HttpStatus.CREATED);
    }
}
```

### 2. Business Logic Layer (Рівень бізнес-логіки)

**Технології:** Spring Services, Transactional Management

**Відповідальність:**
- Бізнес-правила та логіка
- Валідація бізнес-правил
- Координація між різними repositories
- Трансакційне управління

**Компоненти:**
- Service інтерфейси (UserService, ExpenseService, etc.)
- Service реалізації (UserServiceImpl, ExpenseServiceImpl, etc.)

**Приклад застосування SOLID:**
```java
// Interface Segregation Principle
public interface ExpenseService {
    ExpenseDTO createExpense(ExpenseDTO dto);
    ExpenseDTO updateExpense(Long id, ExpenseDTO dto);
    // ... інші методи
}

// Dependency Inversion Principle
@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository repository; // Залежність від абстракції
    private final ModelMapper mapper;

    // Constructor Injection
    public ExpenseServiceImpl(ExpenseRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
}
```

### 3. Data Access Layer (Рівень доступу до даних)

**Технології:** Spring Data JPA, Hibernate

**Відповідальність:**
- CRUD операції
- Запити до бази даних
- Кешування
- Управління сесіями

**Компоненти:**
- Repository інтерфейси
- Custom queries

**Приклад:**
```java
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalExpenses(@Param("userId") Long userId);
}
```

### 4. Database Layer (Рівень бази даних)

**Технології:** PostgreSQL

**Відповідальність:**
- Зберігання даних
- Забезпечення цілісності даних
- Індексування
- Транзакції

## Застосування принципів SOLID

### Single Responsibility Principle (SRP)

Кожен клас має одну чітко визначену відповідальність:

- **Controllers** - тільки обробка HTTP запитів
- **Services** - тільки бізнес-логіка
- **Repositories** - тільки доступ до даних
- **Entities** - тільки представлення даних
- **DTOs** - тільки передача даних

### Open/Closed Principle (OCP)

Класи відкриті для розширення, але закриті для модифікації:

```java
// Інтерфейс визначає контракт
public interface ExpenseService {
    ExpenseDTO createExpense(ExpenseDTO dto);
}

// Можна створити нову реалізацію без зміни існуючого коду
public class AdvancedExpenseServiceImpl implements ExpenseService {
    @Override
    public ExpenseDTO createExpense(ExpenseDTO dto) {
        // Розширена логіка
    }
}
```

### Liskov Substitution Principle (LSP)

Дочірні класи можуть заміняти батьківські:

```java
ExpenseService service = new ExpenseServiceImpl(...);
// або
ExpenseService service = new AdvancedExpenseServiceImpl(...);
// Обидва працюють однаково для клієнта
```

### Interface Segregation Principle (ISP)

Інтерфейси розділені на специфічні:

```java
// Замість одного великого інтерфейсу
public interface ExpenseService {
    ExpenseDTO createExpense(ExpenseDTO dto);
    ExpenseDTO updateExpense(Long id, ExpenseDTO dto);
}

public interface ExpenseQueryService {
    List<ExpenseDTO> findByUserId(Long userId);
    BigDecimal getTotalExpenses(Long userId);
}
```

### Dependency Inversion Principle (DIP)

Залежність від абстракцій, а не конкретних реалізацій:

```java
@Service
public class ExpenseServiceImpl implements ExpenseService {
    // Залежність від інтерфейсу Repository, а не конкретної реалізації
    private final ExpenseRepository repository;
    // Залежність від інтерфейсу ModelMapper
    private final ModelMapper mapper;
}
```

## Патерни проектування

### 1. Repository Pattern

**Мета:** Абстрагування логіки доступу до даних

**Реалізація:**
```java
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    // Spring Data JPA автоматично створює реалізацію
}
```

**Переваги:**
- Централізація логіки доступу до даних
- Легке тестування через mock об'єкти
- Можливість зміни реалізації БД

### 2. Service Layer Pattern

**Мета:** Інкапсуляція бізнес-логіки

**Реалізація:**
```java
@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    // Бізнес-логіка тут
}
```

**Переваги:**
- Чітке розділення відповідальностей
- Можливість повторного використання логіки
- Трансакційне управління

### 3. Data Transfer Object (DTO) Pattern

**Мета:** Передача даних між шарами

**Реалізація:**
```java
public class ExpenseDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    // ... інші поля
}
```

**Переваги:**
- Приховування внутрішньої структури Entity
- Оптимізація передачі даних
- Валідація на рівні API

### 4. Dependency Injection Pattern

**Мета:** Управління залежностями

**Реалізація:**
```java
@Service
@RequiredArgsConstructor // Lombok генерує constructor
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository repository;
    private final ModelMapper mapper;
}
```

**Переваги:**
- Слабка зв'язаність
- Легке тестування
- Гнучкість конфігурації

### 5. Factory Pattern

**Мета:** Створення об'єктів

**Реалізація:**
```java
@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
```

### 6. Strategy Pattern

**Мета:** Визначення різних алгоритмів

**Можлива реалізація для різних способів розрахунку бюджету:**
```java
public interface BudgetCalculationStrategy {
    BigDecimal calculate(Budget budget, List<Expense> expenses);
}

public class MonthlyBudgetStrategy implements BudgetCalculationStrategy {
    // Реалізація для місячного бюджету
}

public class WeeklyBudgetStrategy implements BudgetCalculationStrategy {
    // Реалізація для тижневого бюджету
}
```

## Frontend Architecture

### Component-Based Architecture

React компоненти організовані за функціональністю:

```
components/
├── Dashboard.js          # Головна сторінка
├── ExpenseList.js        # Список витрат
├── ExpenseForm.js        # Форма витрат
└── CategoryManagement.js # Управління категоріями
```

### State Management

Локальний state через React Hooks:
- `useState` - для локального стану
- `useEffect` - для side effects
- `useCallback` - для оптимізації

### API Communication

Централізований API клієнт:
```javascript
// services/api.js
export const expenseAPI = {
    createExpense: (data) => api.post('/expenses', data),
    getExpenses: (userId) => api.get(`/expenses/user/${userId}`),
    // ...
}
```

## Data Flow

### Create Expense Flow

```
User Input → Frontend → REST API → Controller → Service → Repository → Database
                                        ↓
                                    Validation
                                        ↓
                                  Business Logic
                                        ↓
                                   DTO Conversion
```

### View Dashboard Flow

```
Frontend Request → Multiple API Calls → Controllers → Services → Repositories → Database
                        ↓
                  Data Aggregation
                        ↓
                    Rendering
```

## Security Considerations

### Input Validation

- Backend: `@Valid` annotation з Bean Validation
- Frontend: Form validation

### Error Handling

- Global exception handler
- Централізована обробка помилок
- User-friendly error messages

### Database Security

- Prepared statements (через JPA)
- SQL injection protection
- Transaction management

## Performance Optimization

### Backend

- Lazy loading для relationships
- Query optimization
- Connection pooling
- Caching strategies

### Frontend

- Code splitting
- Lazy loading components
- Memoization
- Virtual scrolling для великих списків

## Testing Strategy

### Backend Testing

- **Unit Tests:** Services
- **Integration Tests:** Controllers
- **Repository Tests:** Database queries

### Frontend Testing

- **Component Tests:** React components
- **Integration Tests:** User flows
- **E2E Tests:** Full application flow

## Deployment Architecture

```
┌─────────────────────┐
│   Nginx (Frontend)  │ :3000
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│  Spring Boot API    │ :8080
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│    PostgreSQL       │ :5432
└─────────────────────┘
```

## Scalability

### Horizontal Scaling

- Stateless backend (можна запустити multiple instances)
- Database connection pooling
- Load balancing готовність

### Vertical Scaling

- Оптимізація запитів
- Індексування БД
- Кешування

## Monitoring and Logging

### Backend

- Spring Boot Actuator
- Health checks
- Metrics endpoints

### Frontend

- Console logging
- Error boundaries
- Performance monitoring

## Conclusion

Архітектура Smart Expense Tracker побудована на перевірених принципах та патернах, що забезпечує:

- **Maintainability** - легкість підтримки
- **Scalability** - можливість масштабування
- **Testability** - простота тестування
- **Flexibility** - гнучкість до змін
- **Reliability** - надійність роботи
