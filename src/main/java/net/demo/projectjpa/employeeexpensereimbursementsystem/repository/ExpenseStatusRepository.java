package net.demo.projectjpa.employeeexpensereimbursementsystem.repository;

import net.demo.projectjpa.employeeexpensereimbursementsystem.model.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseStatusRepository extends JpaRepository<ExpenseStatus, Integer> {
}
