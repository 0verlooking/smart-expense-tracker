#!/bin/bash

# Smart Expense Tracker System Check Script
# This script checks if all services are running correctly

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "================================================"
echo "  Smart Expense Tracker - System Check"
echo "================================================"
echo ""

# Function to check command status
check_status() {
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ $1${NC}"
    else
        echo -e "${RED}✗ $1${NC}"
        exit 1
    fi
}

# 1. Check Docker
echo "1. Checking Docker installation..."
docker --version > /dev/null 2>&1
check_status "Docker is installed"

docker compose version > /dev/null 2>&1
check_status "Docker Compose is available"
echo ""

# 2. Check containers
echo "2. Checking Docker containers..."
docker compose ps
echo ""

# Count running containers
RUNNING=$(docker compose ps | grep -c "Up" || true)
echo "Running containers: $RUNNING/3"
echo ""

# 3. Check PostgreSQL
echo "3. Checking PostgreSQL..."
docker exec expense-tracker-db pg_isready -U postgres > /dev/null 2>&1
check_status "PostgreSQL is ready"

# Check database exists
DB_EXISTS=$(docker exec expense-tracker-db psql -U postgres -lqt | cut -d \| -f 1 | grep -w expense_tracker_db | wc -l)
if [ "$DB_EXISTS" -eq 1 ]; then
    echo -e "${GREEN}✓ Database 'expense_tracker_db' exists${NC}"
else
    echo -e "${YELLOW}! Database 'expense_tracker_db' not found${NC}"
fi
echo ""

# 4. Check Backend
echo "4. Checking Backend (Spring Boot)..."
BACKEND_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || echo "000")

if [ "$BACKEND_HEALTH" = "200" ]; then
    echo -e "${GREEN}✓ Backend is healthy (HTTP $BACKEND_HEALTH)${NC}"

    # Check Swagger
    SWAGGER=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui/index.html || echo "000")
    if [ "$SWAGGER" = "200" ]; then
        echo -e "${GREEN}✓ Swagger UI is accessible${NC}"
    fi

    # Test API endpoint
    API_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/users || echo "000")
    echo "  API /api/users responds with HTTP $API_TEST"
else
    echo -e "${RED}✗ Backend is not responding (HTTP $BACKEND_HEALTH)${NC}"
    echo "  Please check backend logs: docker logs expense-tracker-backend"
fi
echo ""

# 5. Check Frontend
echo "5. Checking Frontend (React)..."
FRONTEND=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000 || echo "000")

if [ "$FRONTEND" = "200" ]; then
    echo -e "${GREEN}✓ Frontend is accessible (HTTP $FRONTEND)${NC}"
else
    echo -e "${RED}✗ Frontend is not responding (HTTP $FRONTEND)${NC}"
fi
echo ""

# 6. Check volumes
echo "6. Checking Docker volumes..."
VOLUME_EXISTS=$(docker volume ls | grep -c postgres_data || true)
if [ "$VOLUME_EXISTS" -eq 1 ]; then
    echo -e "${GREEN}✓ PostgreSQL volume exists${NC}"
else
    echo -e "${YELLOW}! PostgreSQL volume not found${NC}"
fi
echo ""

# 7. Check network
echo "7. Checking Docker network..."
NETWORK_EXISTS=$(docker network ls | grep -c expense-tracker-network || true)
if [ "$NETWORK_EXISTS" -eq 1 ]; then
    echo -e "${GREEN}✓ Docker network exists${NC}"
else
    echo -e "${RED}✗ Docker network not found${NC}"
fi
echo ""

# Summary
echo "================================================"
echo "  Summary"
echo "================================================"
if [ "$BACKEND_HEALTH" = "200" ] && [ "$FRONTEND" = "200" ]; then
    echo -e "${GREEN}✓ All systems operational!${NC}"
    echo ""
    echo "Access your application:"
    echo "  • Frontend:    http://localhost:3000"
    echo "  • Backend API: http://localhost:8080/api"
    echo "  • Swagger UI:  http://localhost:8080/swagger-ui.html"
    echo "  • Health:      http://localhost:8080/actuator/health"
else
    echo -e "${YELLOW}⚠ Some services are not running${NC}"
    echo ""
    echo "Quick fix commands:"
    echo "  docker-compose restart backend"
    echo "  docker-compose restart frontend"
    echo ""
    echo "View logs:"
    echo "  docker-compose logs backend"
    echo "  docker-compose logs frontend"
    echo ""
    echo "For more help, see TROUBLESHOOTING.md"
fi
echo ""
