-- Initial data for Smart Expense Tracker
-- This script will recreate all tables and populate them with test data

-- Drop old tables if they exist (for clean migration)
DROP TABLE IF EXISTS expenses CASCADE;
DROP TABLE IF EXISTS budgets CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Recreate tables with proper schema including authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(255) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    icon_name VARCHAR(255),
    color_code VARCHAR(255),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(10,2) NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(500),
    merchant VARCHAR(255),
    payment_method VARCHAR(255) CHECK (payment_method IN ('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER', 'OTHER')),
    notes TEXT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(10,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    budget_name VARCHAR(255),
    period_type VARCHAR(255) CHECK (period_type IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY', 'CUSTOM')),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert test users with BCrypt hashed passwords (strength 10)
-- Admin: username=admin, password=admin123
-- User: username=user, password=user123
-- Note: BCrypt hashes are salted and will be different each time they're generated

INSERT INTO users (id, username, email, password, full_name, role, created_at, updated_at)
VALUES
    (1, 'admin', 'admin@expensetracker.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye8IZGBjPi8AhULDLl3cZKGYZHvLt9bLO', 'Administrator', 'ADMIN', NOW(), NOW()),
    (2, 'user', 'user@expensetracker.com', '$2a$10$1l8IHPPsE1zARPNJWbLIHe8nZQhkLqCXWlRkPZQREP/ynQHV.uPZi', 'John Doe', 'USER', NOW(), NOW());

-- Insert categories for user (id=2)
INSERT INTO categories (id, name, description, icon_name, color_code, user_id, created_at)
VALUES
    (1, 'Food & Dining', 'Restaurants, groceries, and food delivery', '🍔', '#FF6B6B', 2, NOW()),
    (2, 'Transportation', 'Gas, public transport, car maintenance', '🚗', '#4ECDC4', 2, NOW()),
    (3, 'Shopping', 'Clothes, electronics, and other purchases', '🛍️', '#95E1D3', 2, NOW()),
    (4, 'Healthcare', 'Medical expenses and insurance', '💊', '#F38181', 2, NOW()),
    (5, 'Utilities', 'Electricity, water, internet, phone', '💡', '#AA96DA', 2, NOW()),
    (6, 'Entertainment', 'Movies, games, subscriptions', '🎬', '#FCBAD3', 2, NOW());

-- Insert sample expenses for user (id=2)
INSERT INTO expenses (id, amount, date, description, merchant, payment_method, notes, user_id, category_id, created_at, updated_at)
VALUES
    (1, 45.50, CURRENT_DATE - INTERVAL '1 day', 'Grocery shopping', 'Supermarket', 'DEBIT_CARD', 'Weekly groceries', 2, 1, NOW(), NOW()),
    (2, 25.00, CURRENT_DATE - INTERVAL '2 days', 'Gas refill', 'Gas Station', 'CREDIT_CARD', 'Fuel for car', 2, 2, NOW(), NOW()),
    (3, 120.00, CURRENT_DATE - INTERVAL '3 days', 'New shoes', 'Shoe Store', 'CREDIT_CARD', 'Running shoes', 2, 3, NOW(), NOW()),
    (4, 15.75, CURRENT_DATE - INTERVAL '4 days', 'Lunch', 'Restaurant', 'CASH', 'Business lunch', 2, 1, NOW(), NOW()),
    (5, 80.00, CURRENT_DATE - INTERVAL '5 days', 'Internet bill', 'ISP Provider', 'BANK_TRANSFER', 'Monthly internet', 2, 5, NOW(), NOW()),
    (6, 12.99, CURRENT_DATE - INTERVAL '6 days', 'Netflix subscription', 'Netflix', 'CREDIT_CARD', 'Monthly subscription', 2, 6, NOW(), NOW()),
    (7, 35.20, CURRENT_DATE - INTERVAL '7 days', 'Dinner with friends', 'Italian Restaurant', 'CREDIT_CARD', 'Friday dinner', 2, 1, NOW(), NOW()),
    (8, 50.00, CURRENT_DATE, 'Pharmacy', 'Local Pharmacy', 'DEBIT_CARD', 'Medications', 2, 4, NOW(), NOW());

-- Insert sample budgets for user (id=2)
INSERT INTO budgets (id, amount, start_date, end_date, budget_name, period_type, user_id, created_at, updated_at)
VALUES
    (1, 2000.00, DATE_TRUNC('month', CURRENT_DATE), DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day', 'Monthly Budget', 'MONTHLY', 2, NOW(), NOW()),
    (2, 500.00, DATE_TRUNC('week', CURRENT_DATE), DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '1 week' - INTERVAL '1 day', 'Weekly Budget', 'WEEKLY', 2, NOW(), NOW());

-- Reset sequences
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users), true);
SELECT setval('categories_id_seq', (SELECT COALESCE(MAX(id), 1) FROM categories), true);
SELECT setval('expenses_id_seq', (SELECT COALESCE(MAX(id), 1) FROM expenses), true);
SELECT setval('budgets_id_seq', (SELECT COALESCE(MAX(id), 1) FROM budgets), true);
