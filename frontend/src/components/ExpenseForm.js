import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  MenuItem,
  Grid,
} from '@mui/material';
import { expenseAPI, categoryAPI } from '../services/api';

function ExpenseForm({ open, onClose, userId, expense, onSuccess }) {
  const [formData, setFormData] = useState({
    amount: '',
    date: new Date().toISOString().split('T')[0],
    description: '',
    merchant: '',
    paymentMethod: 'CASH',
    notes: '',
    categoryId: '',
  });
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    if (userId) {
      fetchCategories();
    }
  }, [userId]);

  useEffect(() => {
    if (expense) {
      setFormData({
        amount: expense.amount,
        date: expense.date,
        description: expense.description || '',
        merchant: expense.merchant || '',
        paymentMethod: expense.paymentMethod || 'CASH',
        notes: expense.notes || '',
        categoryId: expense.categoryId,
      });
    }
  }, [expense]);

  const fetchCategories = async () => {
    try {
      const response = await categoryAPI.getAllCategoriesByUserId(userId);
      setCategories(response.data);
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const expenseData = {
        ...formData,
        userId,
      };

      if (expense) {
        await expenseAPI.updateExpense(expense.id, expenseData);
      } else {
        await expenseAPI.createExpense(expenseData);
      }

      onSuccess && onSuccess();
      handleClose();
    } catch (error) {
      console.error('Error saving expense:', error);
    }
  };

  const handleClose = () => {
    setFormData({
      amount: '',
      date: new Date().toISOString().split('T')[0],
      description: '',
      merchant: '',
      paymentMethod: 'CASH',
      notes: '',
      categoryId: '',
    });
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>{expense ? 'Edit Expense' : 'Add New Expense'}</DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Amount"
                name="amount"
                type="number"
                inputProps={{ step: '0.01' }}
                value={formData.amount}
                onChange={handleChange}
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Date"
                name="date"
                type="date"
                value={formData.date}
                onChange={handleChange}
                InputLabelProps={{ shrink: true }}
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Category"
                name="categoryId"
                value={formData.categoryId}
                onChange={handleChange}
                required
              >
                {categories.map((category) => (
                  <MenuItem key={category.id} value={category.id}>
                    {category.name}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Payment Method"
                name="paymentMethod"
                value={formData.paymentMethod}
                onChange={handleChange}
              >
                <MenuItem value="CASH">Cash</MenuItem>
                <MenuItem value="CREDIT_CARD">Credit Card</MenuItem>
                <MenuItem value="DEBIT_CARD">Debit Card</MenuItem>
                <MenuItem value="BANK_TRANSFER">Bank Transfer</MenuItem>
                <MenuItem value="MOBILE_PAYMENT">Mobile Payment</MenuItem>
                <MenuItem value="OTHER">Other</MenuItem>
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Merchant"
                name="merchant"
                value={formData.merchant}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                multiline
                rows={2}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Notes"
                name="notes"
                value={formData.notes}
                onChange={handleChange}
                multiline
                rows={2}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button type="submit" variant="contained" color="primary">
            {expense ? 'Update' : 'Add'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}

export default ExpenseForm;
