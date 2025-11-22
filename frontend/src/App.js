import React, { useState } from 'react';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link,
  Navigate,
  useNavigate,
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
  Button,
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  Receipt,
  Category,
  AccountBalance,
  Add,
  AdminPanelSettings,
  Logout,
} from '@mui/icons-material';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './components/Login';
import Register from './components/Register';
import ProtectedRoute from './components/ProtectedRoute';
import AdminPanel from './components/AdminPanel';
import Dashboard from './components/Dashboard';
import ExpenseList from './components/ExpenseList';
import CategoryManagement from './components/CategoryManagement';
import ExpenseForm from './components/ExpenseForm';
import './App.css';

const drawerWidth = 240;

function MainApp() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
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

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const menuItems = [
    { text: 'Dashboard', icon: <DashboardIcon />, path: '/' },
    { text: 'Expenses', icon: <Receipt />, path: '/expenses' },
    { text: 'Categories', icon: <Category />, path: '/categories' },
  ];

  if (isAdmin()) {
    menuItems.push({ text: 'Admin Panel', icon: <AdminPanelSettings />, path: '/admin' });
  }

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}
      >
        <Toolbar>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            Smart Expense Tracker
          </Typography>
          {user && (
            <>
              <Typography variant="body1" sx={{ mr: 2 }}>
                {user.username}
              </Typography>
              <Button
                color="inherit"
                startIcon={<Logout />}
                onClick={handleLogout}
              >
                Logout
              </Button>
            </>
          )}
        </Toolbar>
      </AppBar>
      {user && (
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
      )}
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
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Dashboard userId={user?.id} />
              </ProtectedRoute>
            }
          />
          <Route
            path="/expenses"
            element={
              <ProtectedRoute>
                <ExpenseList
                  userId={user?.id}
                  onEdit={handleOpenExpenseForm}
                  onRefresh={refreshKey}
                />
              </ProtectedRoute>
            }
          />
          <Route
            path="/categories"
            element={
              <ProtectedRoute>
                <CategoryManagement userId={user?.id} />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <ProtectedRoute adminOnly={true}>
                <AdminPanel />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>

        {user && (
          <>
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
              userId={user?.id}
              expense={selectedExpense}
              onSuccess={handleExpenseSuccess}
            />
          </>
        )}
      </Box>
    </Box>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <MainApp />
      </AuthProvider>
    </Router>
  );
}

export default App;
