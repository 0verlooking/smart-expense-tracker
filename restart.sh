#!/bin/bash

echo "🔄 Restarting Smart Expense Tracker..."
echo ""

echo "1️⃣ Stopping containers..."
docker-compose down

echo ""
echo "2️⃣ Removing old volumes (database will be recreated)..."
docker volume rm smart-expense-tracker_postgres_data 2>/dev/null || true

echo ""
echo "3️⃣ Rebuilding and starting containers..."
docker-compose up --build -d

echo ""
echo "✅ Restart complete!"
echo ""
echo "📊 Services:"
echo "   - Frontend: http://localhost:3000"
echo "   - Backend:  http://localhost:8080"
echo "   - Swagger:  http://localhost:8080/swagger-ui.html"
echo ""
echo "👤 Test Accounts:"
echo "   - Admin: username=admin, password=admin123"
echo "   - User:  username=user, password=user123"
echo ""
echo "📝 Check logs with: docker-compose logs -f"
