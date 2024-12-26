package net.demo.projectjpa.employeeexpensereimbursementsystem.repository;

import net.demo.projectjpa.employeeexpensereimbursementsystem.model.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseStatusRepository extends JpaRepository<ExpenseStatus, Integer> {

    // Custom method to find ExpenseStatus by name
    public List<ExpenseStatus> findByName(String name);
}
