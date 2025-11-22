import React, { createContext, useState, useContext, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on mount
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');

    if (token && userData) {
      setUser(JSON.parse(userData));
      // Set default Authorization header
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        username,
        password,
      });

      const { token, userId, ...rest } = response.data;
      const userData = { id: userId, ...rest };

      // Save to localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));

      // Set default Authorization header
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      setUser(userData);
      return userData;
    } catch (error) {
      throw error;
    }
  };

  const register = async (username, email, password, fullName) => {
    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', {
        username,
        email,
        password,
        fullName,
      });

      const { token, userId, ...rest } = response.data;
      const userData = { id: userId, ...rest };

      // Save to localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));

      // Set default Authorization header
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      setUser(userData);
      return userData;
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    delete axios.defaults.headers.common['Authorization'];
    setUser(null);
  };

  const isAdmin = () => {
    return user && user.role === 'ADMIN';
  };

  const value = {
    user,
    loading,
    login,
    register,
    logout,
    isAdmin,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
