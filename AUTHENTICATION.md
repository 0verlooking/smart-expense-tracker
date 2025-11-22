# Authentication and Authorization Guide

## Overview

The Smart Expense Tracker implements a complete JWT-based authentication and authorization system with role-based access control (RBAC). The system supports two user roles: **USER** and **ADMIN**.

## Architecture

### Backend Components

#### Security Layer
- **Spring Security** - Framework for authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication mechanism
- **BCrypt** - Password hashing algorithm (strength 10)

#### Key Classes

1. **SecurityConfig** (`com.expensetracker.security.SecurityConfig`)
   - Configures Spring Security with JWT authentication
   - Defines role-based access rules for endpoints
   - Configures CORS for frontend communication
   - Disables CSRF (stateless JWT authentication)

2. **JwtUtil** (`com.expensetracker.security.JwtUtil`)
   - Generates JWT tokens with 24-hour expiration
   - Validates tokens and extracts user information
   - Uses HS256 signing algorithm

3. **JwtAuthenticationFilter** (`com.expensetracker.security.JwtAuthenticationFilter`)
   - Intercepts HTTP requests
   - Extracts and validates JWT from Authorization header
   - Sets authentication in SecurityContext

4. **CustomUserDetailsService** (`com.expensetracker.security.CustomUserDetailsService`)
   - Loads user details from database
   - Implements Spring Security's UserDetailsService

5. **Role** (`com.expensetracker.model.Role`)
   - Enum defining user roles: USER, ADMIN
   - Used for role-based access control

### Frontend Components

1. **AuthContext** (`frontend/src/context/AuthContext.js`)
   - Global authentication state management
   - Stores user info and JWT token in localStorage
   - Provides login, register, logout functions
   - Auto-configures Axios with Authorization header

2. **Login** (`frontend/src/components/Login.js`)
   - Login form with username/password
   - Displays test account credentials
   - Redirects to home page on success

3. **Register** (`frontend/src/components/Register.js`)
   - Registration form for new users
   - Creates user with default USER role
   - Auto-login after registration

4. **ProtectedRoute** (`frontend/src/components/ProtectedRoute.js`)
   - HOC for protecting routes
   - Redirects to /login if not authenticated
   - Supports adminOnly flag for admin-restricted routes

5. **AdminPanel** (`frontend/src/components/AdminPanel.js`)
   - Admin interface for user management
   - View all users in the system
   - Edit user roles (USER ↔ ADMIN)
   - Delete users

## API Endpoints

### Public Endpoints

#### Register New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "fullName": "John Doe"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 3,
  "username": "newuser",
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "username": "admin",
  "email": "admin@expensetracker.com",
  "fullName": "Administrator",
  "role": "ADMIN"
}
```

### Protected Endpoints (Requires Authentication)

All endpoints except `/api/auth/**` require a valid JWT token in the Authorization header:

```http
Authorization: Bearer <token>
```

### Admin-Only Endpoints

#### Get All Users
```http
GET /api/admin/users
Authorization: Bearer <admin-token>

Response:
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@expensetracker.com",
    "fullName": "Administrator",
    "role": "ADMIN",
    "createdAt": "2025-11-22T12:00:00"
  },
  ...
]
```

#### Update User Role
```http
PUT /api/admin/users/{id}/role
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "role": "ADMIN"
}

Response:
{
  "id": 2,
  "username": "user",
  "email": "user@expensetracker.com",
  "fullName": "John Doe",
  "role": "ADMIN",
  "createdAt": "2025-11-22T12:00:00"
}
```

#### Delete User
```http
DELETE /api/admin/users/{id}
Authorization: Bearer <admin-token>

Response: 204 No Content
```

## Test Accounts

Two test accounts are pre-configured in the system:

### Admin Account
- **Username:** admin
- **Password:** admin123
- **Role:** ADMIN
- **Email:** admin@expensetracker.com
- **Capabilities:**
  - All USER features
  - View all users
  - Edit user roles
  - Delete users

### User Account
- **Username:** user
- **Password:** user123
- **Role:** USER
- **Email:** user@expensetracker.com
- **Capabilities:**
  - Manage own expenses
  - Create/edit categories
  - Set budgets
  - View analytics

## User Flows

### Registration Flow
1. User navigates to `/register`
2. Fills in username, email, password, and full name
3. System creates new user with USER role
4. System generates JWT token
5. User is automatically logged in
6. Redirects to home page

### Login Flow
1. User navigates to `/login`
2. Enters username and password
3. System validates credentials
4. System generates JWT token
5. Token stored in localStorage
6. Token added to all Axios requests
7. User redirects to home page

### Protected Route Access
1. User attempts to access protected route
2. ProtectedRoute checks if user is authenticated
3. If not authenticated → redirect to /login
4. If authenticated but not admin (for admin routes) → redirect to home
5. If authorized → render requested component

### Admin User Management
1. Admin logs in
2. Navigates to `/admin` (visible in sidebar)
3. Views table of all users
4. Can edit user roles via dialog
5. Can delete users with confirmation

## Security Features

### Password Security
- Passwords hashed with BCrypt (strength 10)
- Never stored or transmitted in plain text
- Salt automatically generated per password

### JWT Token Security
- Signed with HS256 algorithm
- Secret key configurable in application.properties
- 24-hour expiration (86400000 ms)
- Contains username as subject
- Validated on every request

### CORS Configuration
- Configured for localhost:3000 (frontend)
- Allows credentials
- Supports: GET, POST, PUT, DELETE, OPTIONS
- All headers allowed

### Role-Based Access Control
- Method-level security with @PreAuthorize
- Admin endpoints protected with hasRole('ADMIN')
- User data isolated per user (userId-based filtering)
- Automatic role prefix (ROLE_) handled by Spring Security

## Configuration

### Backend Configuration
File: `backend/src/main/resources/application.properties`

```properties
# JWT Configuration (add these)
jwt.secret=mySecretKey12345
jwt.expiration=86400000

# CORS Configuration
spring.web.cors.allowed-origins=http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

### Frontend Configuration
File: `frontend/src/context/AuthContext.js`

```javascript
const API_BASE_URL = 'http://localhost:8080/api/auth';
```

## How to Use

### For Development

1. **Start the application:**
   ```bash
   docker-compose up --build
   ```

2. **Access the frontend:**
   - Open http://localhost:3000
   - You'll be redirected to /login

3. **Login as admin:**
   - Username: admin
   - Password: admin123
   - Access all features including Admin Panel

4. **Login as regular user:**
   - Username: user
   - Password: user123
   - Access regular expense tracking features

### For Testing

1. **Test Registration:**
   - Navigate to /register
   - Create a new account
   - Verify auto-login works

2. **Test Role-Based Access:**
   - Login as regular user
   - Verify Admin Panel is not visible in sidebar
   - Try accessing /admin directly (should redirect)
   - Logout and login as admin
   - Verify Admin Panel is visible

3. **Test Admin Features:**
   - Login as admin
   - Go to Admin Panel
   - Change a user's role
   - Verify role change persists

4. **Test Token Expiration:**
   - Login
   - Wait 24 hours (or modify jwt.expiration for faster testing)
   - Try accessing protected route
   - Should redirect to login

## Troubleshooting

### Login fails with 401 Unauthorized
- Check if username and password are correct
- Verify BCrypt hashes in database match generated hashes
- Check backend logs for authentication errors

### Token not being sent with requests
- Check browser localStorage for 'token' key
- Verify AuthContext is wrapping the app
- Check axios interceptor configuration

### CORS errors
- Verify frontend URL in application.properties
- Check CORS configuration in SecurityConfig
- Ensure credentials are allowed

### Admin routes accessible by regular users
- Verify @PreAuthorize annotation on controller methods
- Check user's role in database
- Verify JWT token contains correct role

### Users can't see their data after login
- Check userId is being passed from AuthContext
- Verify userId matches in database
- Check backend filters data by userId

## Security Best Practices

1. **Change JWT Secret:**
   - Use a strong, random secret key in production
   - Store in environment variables, not in code

2. **HTTPS in Production:**
   - Always use HTTPS for production
   - Update CORS configuration for production domain

3. **Token Storage:**
   - Current: localStorage (simple but vulnerable to XSS)
   - Consider: httpOnly cookies for better security

4. **Password Requirements:**
   - Implement password strength validation
   - Add password confirmation field
   - Consider password reset functionality

5. **Rate Limiting:**
   - Add rate limiting to login endpoint
   - Prevent brute force attacks

6. **Token Refresh:**
   - Implement refresh token mechanism
   - Allow extending sessions without re-login

7. **Audit Logging:**
   - Log authentication attempts
   - Track admin actions
   - Monitor suspicious activities

## Future Enhancements

1. **Refresh Tokens** - Allow token renewal without re-login
2. **Password Reset** - Email-based password recovery
3. **Email Verification** - Verify email addresses on registration
4. **2FA (Two-Factor Authentication)** - Additional security layer
5. **Session Management** - View and revoke active sessions
6. **OAuth2 Integration** - Login with Google, GitHub, etc.
7. **Account Lockout** - Temporarily lock after failed attempts
8. **Password History** - Prevent password reuse
