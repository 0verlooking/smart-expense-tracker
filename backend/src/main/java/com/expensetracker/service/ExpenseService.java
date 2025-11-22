package com.expensetracker.service;

import com.expensetracker.dto.ExpenseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Expense operations
 */
public interface ExpenseService {

    ExpenseDTO createExpense(ExpenseDTO expenseDTO);

    ExpenseDTO getExpenseById(Long id);

    List<ExpenseDTO> getAllExpensesByUserId(Long userId);

    List<ExpenseDTO> getExpensesByUserIdAndCategory(Long userId, Long categoryId);

    List<ExpenseDTO> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO);

    void deleteExpense(Long id);

    BigDecimal getTotalExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate);
}
