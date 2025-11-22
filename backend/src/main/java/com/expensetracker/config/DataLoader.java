package com.expensetracker.config;

import com.expensetracker.model.*;
import com.expensetracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Loader to initialize test data with properly hashed passwords
 * Runs after SQL schema creation
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only create data if users don't exist
        if (userRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        log.info("==================================================");
        log.info("Starting test data initialization...");
        log.info("==================================================");

        try {
            // Create users
            User admin = createAdmin();
            User regularUser = createRegularUser();

            // Create categories for regular user
            List<Category> categories = createCategories(regularUser);

            // Create expenses for regular user
            createExpenses(regularUser, categories);

            // Create budgets for regular user
            createBudgets(regularUser);

            log.info("==================================================");
            log.info("✅ Test data initialized successfully!");
            log.info("==================================================");
            log.info("Test Accounts:");
            log.info("  Admin:  username=admin, password=admin123");
            log.info("  User:   username=user, password=user123");
            log.info("==================================================");

        } catch (Exception e) {
            log.error("❌ Error initializing test data", e);
            throw new RuntimeException("Failed to initialize test data", e);
        }
    }

    private User createAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@expensetracker.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("Administrator");
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        admin = userRepository.save(admin);
        log.info("Created admin user: {} (ID: {})", admin.getUsername(), admin.getId());
        return admin;
    }

    private User createRegularUser() {
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@expensetracker.com");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setFullName("John Doe");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        log.info("Created regular user: {} (ID: {})", user.getUsername(), user.getId());
        return user;
    }

    private List<Category> createCategories(User user) {
        List<Category> categories = new ArrayList<>();

        categories.add(createCategory("Food & Dining", "Restaurants, groceries, and food delivery", "🍔", "#FF6B6B", user));
        categories.add(createCategory("Transportation", "Gas, public transport, car maintenance", "🚗", "#4ECDC4", user));
        categories.add(createCategory("Shopping", "Clothes, electronics, and other purchases", "🛍️", "#95E1D3", user));
        categories.add(createCategory("Healthcare", "Medical expenses and insurance", "💊", "#F38181", user));
        categories.add(createCategory("Utilities", "Electricity, water, internet, phone", "💡", "#AA96DA", user));
        categories.add(createCategory("Entertainment", "Movies, games, subscriptions", "🎬", "#FCBAD3", user));

        log.info("Created {} categories for user {}", categories.size(), user.getUsername());
        return categories;
    }

    private Category createCategory(String name, String description, String icon, String color, User user) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIconName(icon);
        category.setColorCode(color);
        category.setUser(user);
        category.setCreatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    private void createExpenses(User user, List<Category> categories) {
        Category foodCategory = categories.stream().filter(c -> c.getName().equals("Food & Dining")).findFirst().orElse(null);
        Category transportCategory = categories.stream().filter(c -> c.getName().equals("Transportation")).findFirst().orElse(null);
        Category shoppingCategory = categories.stream().filter(c -> c.getName().equals("Shopping")).findFirst().orElse(null);
        Category healthCategory = categories.stream().filter(c -> c.getName().equals("Healthcare")).findFirst().orElse(null);
        Category utilitiesCategory = categories.stream().filter(c -> c.getName().equals("Utilities")).findFirst().orElse(null);
        Category entertainmentCategory = categories.stream().filter(c -> c.getName().equals("Entertainment")).findFirst().orElse(null);

        List<Expense> expenses = new ArrayList<>();

        expenses.add(createExpense(new BigDecimal("45.50"), LocalDate.now().minusDays(1), "Grocery shopping", "Supermarket", PaymentMethod.DEBIT_CARD, "Weekly groceries", user, foodCategory));
        expenses.add(createExpense(new BigDecimal("25.00"), LocalDate.now().minusDays(2), "Gas refill", "Gas Station", PaymentMethod.CREDIT_CARD, "Fuel for car", user, transportCategory));
        expenses.add(createExpense(new BigDecimal("120.00"), LocalDate.now().minusDays(3), "New shoes", "Shoe Store", PaymentMethod.CREDIT_CARD, "Running shoes", user, shoppingCategory));
        expenses.add(createExpense(new BigDecimal("15.75"), LocalDate.now().minusDays(4), "Lunch", "Restaurant", PaymentMethod.CASH, "Business lunch", user, foodCategory));
        expenses.add(createExpense(new BigDecimal("80.00"), LocalDate.now().minusDays(5), "Internet bill", "ISP Provider", PaymentMethod.BANK_TRANSFER, "Monthly internet", user, utilitiesCategory));
        expenses.add(createExpense(new BigDecimal("12.99"), LocalDate.now().minusDays(6), "Netflix subscription", "Netflix", PaymentMethod.CREDIT_CARD, "Monthly subscription", user, entertainmentCategory));
        expenses.add(createExpense(new BigDecimal("35.20"), LocalDate.now().minusDays(7), "Dinner with friends", "Italian Restaurant", PaymentMethod.CREDIT_CARD, "Friday dinner", user, foodCategory));
        expenses.add(createExpense(new BigDecimal("50.00"), LocalDate.now(), "Pharmacy", "Local Pharmacy", PaymentMethod.DEBIT_CARD, "Medications", user, healthCategory));

        log.info("Created {} expenses for user {}", expenses.size(), user.getUsername());
    }

    private Expense createExpense(BigDecimal amount, LocalDate date, String description, String merchant,
                                   PaymentMethod paymentMethod, String notes, User user, Category category) {
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setDescription(description);
        expense.setMerchant(merchant);
        expense.setPaymentMethod(paymentMethod);
        expense.setNotes(notes);
        expense.setUser(user);
        expense.setCategory(category);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        return expenseRepository.save(expense);
    }

    private void createBudgets(User user) {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

        // Monthly budget
        Budget monthlyBudget = new Budget();
        monthlyBudget.setAmount(new BigDecimal("2000.00"));
        monthlyBudget.setStartDate(monthStart);
        monthlyBudget.setEndDate(monthEnd);
        monthlyBudget.setBudgetName("Monthly Budget");
        monthlyBudget.setPeriodType(PeriodType.MONTHLY);
        monthlyBudget.setUser(user);
        monthlyBudget.setCreatedAt(LocalDateTime.now());
        monthlyBudget.setUpdatedAt(LocalDateTime.now());
        budgetRepository.save(monthlyBudget);

        // Weekly budget
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);

        Budget weeklyBudget = new Budget();
        weeklyBudget.setAmount(new BigDecimal("500.00"));
        weeklyBudget.setStartDate(weekStart);
        weeklyBudget.setEndDate(weekEnd);
        weeklyBudget.setBudgetName("Weekly Budget");
        weeklyBudget.setPeriodType(PeriodType.WEEKLY);
        weeklyBudget.setUser(user);
        weeklyBudget.setCreatedAt(LocalDateTime.now());
        weeklyBudget.setUpdatedAt(LocalDateTime.now());
        budgetRepository.save(weeklyBudget);

        log.info("Created 2 budgets for user {}", user.getUsername());
    }
}
