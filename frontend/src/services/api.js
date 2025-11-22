import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// User API
export const userAPI = {
  createUser: (userData) => api.post('/users', userData),
  getUserById: (id) => api.get(`/users/${id}`),
  getUserByUsername: (username) => api.get(`/users/username/${username}`),
  getAllUsers: () => api.get('/users'),
  updateUser: (id, userData) => api.put(`/users/${id}`, userData),
  deleteUser: (id) => api.delete(`/users/${id}`),
};

// Expense API
export const expenseAPI = {
  createExpense: (expenseData) => api.post('/expenses', expenseData),
  getExpenseById: (id) => api.get(`/expenses/${id}`),
  getAllExpensesByUserId: (userId) => api.get(`/expenses/user/${userId}`),
  getExpensesByUserIdAndCategory: (userId, categoryId) =>
    api.get(`/expenses/user/${userId}/category/${categoryId}`),
  getExpensesByDateRange: (userId, startDate, endDate) =>
    api.get(`/expenses/user/${userId}/daterange`, {
      params: { startDate, endDate },
    }),
  getTotalExpensesByDateRange: (userId, startDate, endDate) =>
    api.get(`/expenses/user/${userId}/total`, {
      params: { startDate, endDate },
    }),
  updateExpense: (id, expenseData) => api.put(`/expenses/${id}`, expenseData),
  deleteExpense: (id) => api.delete(`/expenses/${id}`),
};

// Category API
export const categoryAPI = {
  createCategory: (categoryData) => api.post('/categories', categoryData),
  getCategoryById: (id) => api.get(`/categories/${id}`),
  getAllCategoriesByUserId: (userId) => api.get(`/categories/user/${userId}`),
  updateCategory: (id, categoryData) => api.put(`/categories/${id}`, categoryData),
  deleteCategory: (id) => api.delete(`/categories/${id}`),
};

// Budget API
export const budgetAPI = {
  createBudget: (budgetData) => api.post('/budgets', budgetData),
  getBudgetById: (id) => api.get(`/budgets/${id}`),
  getAllBudgetsByUserId: (userId) => api.get(`/budgets/user/${userId}`),
  getActiveBudgetsByUserId: (userId) => api.get(`/budgets/user/${userId}/active`),
  updateBudget: (id, budgetData) => api.put(`/budgets/${id}`, budgetData),
  deleteBudget: (id) => api.delete(`/budgets/${id}`),
};

export default api;
