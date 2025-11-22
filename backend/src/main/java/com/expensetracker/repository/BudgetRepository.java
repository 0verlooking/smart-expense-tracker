package com.expensetracker.repository;

import com.expensetracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Budget entity
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdOrderByStartDateDesc(Long userId);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.startDate <= :date AND b.endDate >= :date")
    List<Budget> findActiveBudgetsByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
