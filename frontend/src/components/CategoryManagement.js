import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  IconButton,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Box,
} from '@mui/material';
import { Delete, Edit, Add } from '@mui/icons-material';
import { categoryAPI } from '../services/api';

function CategoryManagement({ userId }) {
  const [categories, setCategories] = useState([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [currentCategory, setCurrentCategory] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    iconName: '',
    colorCode: '#1976d2',
  });

  useEffect(() => {
    fetchCategories();
  }, [userId]);

  const fetchCategories = async () => {
    try {
      const response = await categoryAPI.getAllCategoriesByUserId(userId);
      setCategories(response.data);
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  const handleOpenDialog = (category = null) => {
    if (category) {
      setCurrentCategory(category);
      setFormData({
        name: category.name,
        description: category.description || '',
        iconName: category.iconName || '',
        colorCode: category.colorCode || '#1976d2',
      });
    } else {
      setCurrentCategory(null);
      setFormData({
        name: '',
        description: '',
        iconName: '',
        colorCode: '#1976d2',
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setCurrentCategory(null);
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
      const categoryData = {
        ...formData,
        userId,
      };

      if (currentCategory) {
        await categoryAPI.updateCategory(currentCategory.id, categoryData);
      } else {
        await categoryAPI.createCategory(categoryData);
      }

      fetchCategories();
      handleCloseDialog();
    } catch (error) {
      console.error('Error saving category:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this category?')) {
      try {
        await categoryAPI.deleteCategory(id);
        fetchCategories();
      } catch (error) {
        console.error('Error deleting category:', error);
      }
    }
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">Categories</Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => handleOpenDialog()}
        >
          Add Category
        </Button>
      </Box>

      <Grid container spacing={3}>
        {categories.map((category) => (
          <Grid item xs={12} sm={6} md={4} key={category.id}>
            <Card>
              <CardContent>
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}
                >
                  <Box>
                    <Typography variant="h6" component="div">
                      {category.name}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {category.description}
                    </Typography>
                  </Box>
                  <Box>
                    <IconButton
                      size="small"
                      color="primary"
                      onClick={() => handleOpenDialog(category)}
                    >
                      <Edit />
                    </IconButton>
                    <IconButton
                      size="small"
                      color="error"
                      onClick={() => handleDelete(category.id)}
                    >
                      <Delete />
                    </IconButton>
                  </Box>
                </Box>
                <Box
                  sx={{
                    width: 40,
                    height: 40,
                    borderRadius: '50%',
                    backgroundColor: category.colorCode || '#1976d2',
                    mt: 2,
                  }}
                />
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {categories.length === 0 && (
        <Box sx={{ textAlign: 'center', mt: 4 }}>
          <Typography variant="body1" color="textSecondary">
            No categories found. Add your first category to get started!
          </Typography>
        </Box>
      )}

      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {currentCategory ? 'Edit Category' : 'Add New Category'}
        </DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent>
            <TextField
              fullWidth
              label="Name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              margin="normal"
            />
            <TextField
              fullWidth
              label="Description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              multiline
              rows={2}
              margin="normal"
            />
            <TextField
              fullWidth
              label="Icon Name"
              name="iconName"
              value={formData.iconName}
              onChange={handleChange}
              margin="normal"
            />
            <TextField
              fullWidth
              label="Color Code"
              name="colorCode"
              type="color"
              value={formData.colorCode}
              onChange={handleChange}
              margin="normal"
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDialog}>Cancel</Button>
            <Button type="submit" variant="contained" color="primary">
              {currentCategory ? 'Update' : 'Add'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Container>
  );
}

export default CategoryManagement;
