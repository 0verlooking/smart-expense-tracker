package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Expense operations
 */
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO createdExpense = expenseService.createExpense(expenseDTO);
        return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long id) {
        ExpenseDTO expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getAllExpensesByUserId(@PathVariable Long userId) {
        List<ExpenseDTO> expenses = expenseService.getAllExpensesByUserId(userId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByUserIdAndCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId) {
        List<ExpenseDTO> expenses = expenseService.getExpensesByUserIdAndCategory(userId, categoryId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{userId}/daterange")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ExpenseDTO> expenses = expenseService.getExpensesByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<BigDecimal> getTotalExpensesByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal total = expenseService.getTotalExpensesByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(
            @PathVariable Long id,
            @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO updatedExpense = expenseService.updateExpense(id, expenseDTO);
        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
