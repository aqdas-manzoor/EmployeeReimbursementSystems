package net.demo.projectjpa.employeeexpensereimbursementsystem.repository;

import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
}
