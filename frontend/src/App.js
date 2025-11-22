import React, { useState } from 'react';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link,
  Navigate,
} from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Container,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Box,
  CssBaseline,
  Fab,
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  Receipt,
  Category,
  AccountBalance,
  Add,
} from '@mui/icons-material';
import Dashboard from './components/Dashboard';
import ExpenseList from './components/ExpenseList';
import CategoryManagement from './components/CategoryManagement';
import ExpenseForm from './components/ExpenseForm';
import './App.css';

const drawerWidth = 240;

function App() {
  const [userId] = useState(1); // In production, this would come from authentication
  const [openExpenseForm, setOpenExpenseForm] = useState(false);
  const [selectedExpense, setSelectedExpense] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);

  const handleOpenExpenseForm = (expense = null) => {
    setSelectedExpense(expense);
    setOpenExpenseForm(true);
  };

  const handleCloseExpenseForm = () => {
    setSelectedExpense(null);
    setOpenExpenseForm(false);
  };

  const handleExpenseSuccess = () => {
    setRefreshKey((prev) => prev + 1);
  };

  const menuItems = [
    { text: 'Dashboard', icon: <DashboardIcon />, path: '/' },
    { text: 'Expenses', icon: <Receipt />, path: '/expenses' },
    { text: 'Categories', icon: <Category />, path: '/categories' },
  ];

  return (
    <Router>
      <Box sx={{ display: 'flex' }}>
        <CssBaseline />
        <AppBar
          position="fixed"
          sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}
        >
          <Toolbar>
            <Typography variant="h6" noWrap component="div">
              Smart Expense Tracker
            </Typography>
          </Toolbar>
        </AppBar>
        <Drawer
          variant="permanent"
          sx={{
            width: drawerWidth,
            flexShrink: 0,
            '& .MuiDrawer-paper': {
              width: drawerWidth,
              boxSizing: 'border-box',
            },
          }}
        >
          <Toolbar />
          <Box sx={{ overflow: 'auto' }}>
            <List>
              {menuItems.map((item) => (
                <ListItem
                  button
                  key={item.text}
                  component={Link}
                  to={item.path}
                >
                  <ListItemIcon>{item.icon}</ListItemIcon>
                  <ListItemText primary={item.text} />
                </ListItem>
              ))}
            </List>
          </Box>
        </Drawer>
        <Box
          component="main"
          sx={{
            flexGrow: 1,
            bgcolor: 'background.default',
            p: 3,
          }}
        >
          <Toolbar />
          <Routes>
            <Route path="/" element={<Dashboard userId={userId} />} />
            <Route
              path="/expenses"
              element={
                <ExpenseList
                  userId={userId}
                  onEdit={handleOpenExpenseForm}
                  onRefresh={refreshKey}
                />
              }
            />
            <Route
              path="/categories"
              element={<CategoryManagement userId={userId} />}
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>

          <Fab
            color="primary"
            aria-label="add"
            sx={{ position: 'fixed', bottom: 16, right: 16 }}
            onClick={() => handleOpenExpenseForm()}
          >
            <Add />
          </Fab>

          <ExpenseForm
            open={openExpenseForm}
            onClose={handleCloseExpenseForm}
            userId={userId}
            expense={selectedExpense}
            onSuccess={handleExpenseSuccess}
          />
        </Box>
      </Box>
    </Router>
  );
}

export default App;
