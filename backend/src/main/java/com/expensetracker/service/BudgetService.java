package com.expensetracker.service;

import com.expensetracker.dto.BudgetDTO;

import java.util.List;

/**
 * Service interface for Budget operations
 */
public interface BudgetService {

    BudgetDTO createBudget(BudgetDTO budgetDTO);

    BudgetDTO getBudgetById(Long id);

    List<BudgetDTO> getAllBudgetsByUserId(Long userId);

    List<BudgetDTO> getActiveBudgetsByUserId(Long userId);

    BudgetDTO updateBudget(Long id, BudgetDTO budgetDTO);

    void deleteBudget(Long id);
}
