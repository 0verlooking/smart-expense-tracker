import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Paper,
  Typography,
  Box,
  Card,
  CardContent,
} from '@mui/material';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from 'recharts';
import { expenseAPI, budgetAPI } from '../services/api';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

function Dashboard({ userId }) {
  const [expenses, setExpenses] = useState([]);
  const [budgets, setBudgets] = useState([]);
  const [totalExpenses, setTotalExpenses] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, [userId]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [expensesRes, budgetsRes] = await Promise.all([
        expenseAPI.getAllExpensesByUserId(userId),
        budgetAPI.getActiveBudgetsByUserId(userId),
      ]);

      setExpenses(expensesRes.data);
      setBudgets(budgetsRes.data);

      const total = expensesRes.data.reduce(
        (sum, expense) => sum + parseFloat(expense.amount),
        0
      );
      setTotalExpenses(total);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getExpensesByCategory = () => {
    const categoryMap = new Map();
    expenses.forEach((expense) => {
      const category = expense.categoryName || 'Uncategorized';
      const current = categoryMap.get(category) || 0;
      categoryMap.set(category, current + parseFloat(expense.amount));
    });

    return Array.from(categoryMap, ([name, value]) => ({ name, value }));
  };

  if (loading) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>

      <Grid container spacing={3}>
        {/* Total Expenses Card */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Total Expenses
              </Typography>
              <Typography variant="h4">
                ₴{totalExpenses.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Total Transactions Card */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Total Transactions
              </Typography>
              <Typography variant="h4">{expenses.length}</Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Active Budgets Card */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Active Budgets
              </Typography>
              <Typography variant="h4">{budgets.length}</Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Expenses by Category Chart */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 400 }}>
            <Typography variant="h6" gutterBottom>
              Expenses by Category
            </Typography>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={getExpensesByCategory()}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={(entry) => entry.name}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {getExpensesByCategory().map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>

        {/* Budget Overview Chart */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 400 }}>
            <Typography variant="h6" gutterBottom>
              Budget Overview
            </Typography>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={budgets}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="budgetName" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="amount" fill="#8884d8" name="Budget" />
                <Bar dataKey="spent" fill="#82ca9d" name="Spent" />
              </BarChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
}

export default Dashboard;
