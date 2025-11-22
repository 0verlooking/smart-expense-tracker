package com.expensetracker.dto;

import com.expensetracker.model.Expense;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Expense
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private String merchant;
    private Expense.PaymentMethod paymentMethod;
    private String notes;
    private Long userId;
    private Long categoryId;
    private String categoryName;
}
