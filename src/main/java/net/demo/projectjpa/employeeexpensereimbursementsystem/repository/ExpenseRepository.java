package net.demo.projectjpa.employeeexpensereimbursementsystem.repository;

import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Expense;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    // Custom query to find expenses by status
    List<Expense> findByStatus(ExpenseStatus status);

    // Custom query to find expenses by employeeId and date range
    @Query("SELECT e FROM Expense e WHERE e.employee.id = :employeeId AND e.submitDate BETWEEN :startDate AND :endDate")
    List<Expense> findByEmployeeIdAndDateRange(int employeeId, LocalDateTime startDate, LocalDateTime endDate);

}
