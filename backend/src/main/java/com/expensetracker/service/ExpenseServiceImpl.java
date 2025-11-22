package com.expensetracker.service;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ExpenseService
 * Follows SOLID principles
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        User user = userRepository.findById(expenseDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + expenseDTO.getUserId()));

        Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + expenseDTO.getCategoryId()));

        Expense expense = new Expense();
        expense.setAmount(expenseDTO.getAmount());
        expense.setDate(expenseDTO.getDate());
        expense.setDescription(expenseDTO.getDescription());
        expense.setMerchant(expenseDTO.getMerchant());
        expense.setPaymentMethod(expenseDTO.getPaymentMethod());
        expense.setNotes(expenseDTO.getNotes());
        expense.setUser(user);
        expense.setCategory(category);

        Expense savedExpense = expenseRepository.save(expense);
        return convertToDTO(savedExpense);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        return convertToDTO(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getAllExpensesByUserId(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByUserIdAndCategory(Long userId, Long categoryId) {
        return expenseRepository.findByUserIdAndCategoryId(userId, categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (expenseDTO.getAmount() != null) {
            expense.setAmount(expenseDTO.getAmount());
        }
        if (expenseDTO.getDate() != null) {
            expense.setDate(expenseDTO.getDate());
        }
        if (expenseDTO.getDescription() != null) {
            expense.setDescription(expenseDTO.getDescription());
        }
        if (expenseDTO.getMerchant() != null) {
            expense.setMerchant(expenseDTO.getMerchant());
        }
        if (expenseDTO.getPaymentMethod() != null) {
            expense.setPaymentMethod(expenseDTO.getPaymentMethod());
        }
        if (expenseDTO.getNotes() != null) {
            expense.setNotes(expenseDTO.getNotes());
        }
        if (expenseDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + expenseDTO.getCategoryId()));
            expense.setCategory(category);
        }

        Expense updatedExpense = expenseRepository.save(expense);
        return convertToDTO(updatedExpense);
    }

    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = expenseRepository.getTotalExpensesByUserIdAndDateRange(userId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = modelMapper.map(expense, ExpenseDTO.class);
        dto.setUserId(expense.getUser().getId());
        dto.setCategoryId(expense.getCategory().getId());
        dto.setCategoryName(expense.getCategory().getName());
        return dto;
    }
}
