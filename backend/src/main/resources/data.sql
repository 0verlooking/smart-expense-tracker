-- Initial data for Smart Expense Tracker
-- Run this after the application creates the schema

-- Insert test user
INSERT INTO users (id, username, email, password, full_name, created_at, updated_at)
VALUES (1, 'testuser', 'test@example.com', 'password123', 'Test User', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert categories
INSERT INTO categories (id, name, description, icon_name, color_code, user_id, created_at)
VALUES
    (1, 'Food & Dining', 'Restaurants, groceries, and food delivery', '🍔', '#FF6B6B', 1, NOW()),
    (2, 'Transportation', 'Gas, public transport, car maintenance', '🚗', '#4ECDC4', 1, NOW()),
    (3, 'Shopping', 'Clothes, electronics, and other purchases', '🛍️', '#95E1D3', 1, NOW()),
    (4, 'Healthcare', 'Medical expenses and insurance', '💊', '#F38181', 1, NOW()),
    (5, 'Utilities', 'Electricity, water, internet, phone', '💡', '#AA96DA', 1, NOW()),
    (6, 'Entertainment', 'Movies, games, subscriptions', '🎬', '#FCBAD3', 1, NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample expenses
INSERT INTO expenses (id, amount, date, description, merchant, payment_method, notes, user_id, category_id, created_at, updated_at)
VALUES
    (1, 45.50, CURRENT_DATE - INTERVAL '1 day', 'Grocery shopping', 'Supermarket', 'DEBIT_CARD', 'Weekly groceries', 1, 1, NOW(), NOW()),
    (2, 25.00, CURRENT_DATE - INTERVAL '2 days', 'Gas refill', 'Gas Station', 'CREDIT_CARD', 'Fuel for car', 1, 2, NOW(), NOW()),
    (3, 120.00, CURRENT_DATE - INTERVAL '3 days', 'New shoes', 'Shoe Store', 'CREDIT_CARD', 'Running shoes', 1, 3, NOW(), NOW()),
    (4, 15.75, CURRENT_DATE - INTERVAL '4 days', 'Lunch', 'Restaurant', 'CASH', 'Business lunch', 1, 1, NOW(), NOW()),
    (5, 80.00, CURRENT_DATE - INTERVAL '5 days', 'Internet bill', 'ISP Provider', 'BANK_TRANSFER', 'Monthly internet', 1, 5, NOW(), NOW()),
    (6, 12.99, CURRENT_DATE - INTERVAL '6 days', 'Netflix subscription', 'Netflix', 'CREDIT_CARD', 'Monthly subscription', 1, 6, NOW(), NOW()),
    (7, 35.20, CURRENT_DATE - INTERVAL '7 days', 'Dinner with friends', 'Italian Restaurant', 'CREDIT_CARD', 'Friday dinner', 1, 1, NOW(), NOW()),
    (8, 50.00, CURRENT_DATE, 'Pharmacy', 'Local Pharmacy', 'DEBIT_CARD', 'Medications', 1, 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample budgets
INSERT INTO budgets (id, amount, start_date, end_date, budget_name, period_type, user_id, created_at, updated_at)
VALUES
    (1, 2000.00, DATE_TRUNC('month', CURRENT_DATE), DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month' - INTERVAL '1 day', 'Monthly Budget', 'MONTHLY', 1, NOW(), NOW()),
    (2, 500.00, DATE_TRUNC('week', CURRENT_DATE), DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '1 week' - INTERVAL '1 day', 'Weekly Budget', 'WEEKLY', 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequences
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users), true);
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories), true);
SELECT setval('expenses_id_seq', (SELECT MAX(id) FROM expenses), true);
SELECT setval('budgets_id_seq', (SELECT MAX(id) FROM budgets), true);
