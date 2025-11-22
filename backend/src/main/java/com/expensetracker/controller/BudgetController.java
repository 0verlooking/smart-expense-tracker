package com.expensetracker.controller;

import com.expensetracker.dto.BudgetDTO;
import com.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Budget operations
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@Valid @RequestBody BudgetDTO budgetDTO) {
        BudgetDTO createdBudget = budgetService.createBudget(budgetDTO);
        return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDTO> getBudgetById(@PathVariable Long id) {
        BudgetDTO budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetDTO>> getAllBudgetsByUserId(@PathVariable Long userId) {
        List<BudgetDTO> budgets = budgetService.getAllBudgetsByUserId(userId);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<BudgetDTO>> getActiveBudgetsByUserId(@PathVariable Long userId) {
        List<BudgetDTO> budgets = budgetService.getActiveBudgetsByUserId(userId);
        return ResponseEntity.ok(budgets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO> updateBudget(
            @PathVariable Long id,
            @RequestBody BudgetDTO budgetDTO) {
        BudgetDTO updatedBudget = budgetService.updateBudget(id, budgetDTO);
        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
