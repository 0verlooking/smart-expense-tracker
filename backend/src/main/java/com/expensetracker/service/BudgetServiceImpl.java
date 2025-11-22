package com.expensetracker.service;

import com.expensetracker.dto.BudgetDTO;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.Budget;
import com.expensetracker.model.User;
import com.expensetracker.repository.BudgetRepository;
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
 * Implementation of BudgetService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ModelMapper modelMapper;

    @Override
    public BudgetDTO createBudget(BudgetDTO budgetDTO) {
        User user = userRepository.findById(budgetDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + budgetDTO.getUserId()));

        Budget budget = new Budget();
        budget.setAmount(budgetDTO.getAmount());
        budget.setStartDate(budgetDTO.getStartDate());
        budget.setEndDate(budgetDTO.getEndDate());
        budget.setBudgetName(budgetDTO.getBudgetName());
        budget.setPeriodType(budgetDTO.getPeriodType());
        budget.setUser(user);

        Budget savedBudget = budgetRepository.save(budget);
        return convertToDTO(savedBudget);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetDTO getBudgetById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        return convertToDTO(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDTO> getAllBudgetsByUserId(Long userId) {
        return budgetRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDTO> getActiveBudgetsByUserId(Long userId) {
        LocalDate today = LocalDate.now();
        return budgetRepository.findActiveBudgetsByUserIdAndDate(userId, today).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BudgetDTO updateBudget(Long id, BudgetDTO budgetDTO) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        if (budgetDTO.getAmount() != null) {
            budget.setAmount(budgetDTO.getAmount());
        }
        if (budgetDTO.getStartDate() != null) {
            budget.setStartDate(budgetDTO.getStartDate());
        }
        if (budgetDTO.getEndDate() != null) {
            budget.setEndDate(budgetDTO.getEndDate());
        }
        if (budgetDTO.getBudgetName() != null) {
            budget.setBudgetName(budgetDTO.getBudgetName());
        }
        if (budgetDTO.getPeriodType() != null) {
            budget.setPeriodType(budgetDTO.getPeriodType());
        }

        Budget updatedBudget = budgetRepository.save(budget);
        return convertToDTO(updatedBudget);
    }

    @Override
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }

    private BudgetDTO convertToDTO(Budget budget) {
        BudgetDTO dto = modelMapper.map(budget, BudgetDTO.class);
        dto.setUserId(budget.getUser().getId());

        BigDecimal spent = expenseRepository.getTotalExpensesByUserIdAndDateRange(
                budget.getUser().getId(),
                budget.getStartDate(),
                budget.getEndDate()
        );
        spent = spent != null ? spent : BigDecimal.ZERO;

        dto.setSpent(spent);
        dto.setRemaining(budget.getAmount().subtract(spent));

        return dto;
    }
}
