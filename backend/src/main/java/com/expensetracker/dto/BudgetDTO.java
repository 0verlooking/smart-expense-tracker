package com.expensetracker.dto;

import com.expensetracker.model.Budget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Budget
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String budgetName;
    private Budget.PeriodType periodType;
    private Long userId;
    private BigDecimal spent;
    private BigDecimal remaining;
}
