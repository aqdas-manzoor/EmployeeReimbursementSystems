package net.demo.projectjpa.employeeexpensereimbursementsystem.service;

import jakarta.persistence.EntityNotFoundException;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Categories;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Employee;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Expense;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.ExpenseStatus;
import net.demo.projectjpa.employeeexpensereimbursementsystem.repository.CategoriesRepository;
import net.demo.projectjpa.employeeexpensereimbursementsystem.repository.EmployeeRepository;
import net.demo.projectjpa.employeeexpensereimbursementsystem.repository.ExpenseRepository;
import net.demo.projectjpa.employeeexpensereimbursementsystem.repository.ExpenseStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmployeeService {


    private final ExpenseRepository expenseRepository;
    private final ExpenseStatusRepository expenseStatusRepository;
    private final CategoriesRepository categoriesRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(ExpenseRepository expenseRepository, ExpenseStatusRepository expenseStatusRepository, CategoriesRepository categoriesRepository, EmployeeRepository employeeRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseStatusRepository = expenseStatusRepository;
        this.categoriesRepository = categoriesRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     *
     *  Submits a new expense for an employee by associating it with the employee, category, and status.
     *  It fetches the employee, category, and status by their IDs, throwing an {@link EntityNotFoundException}
     *  if any are not found. The expense is then saved with the current submit date.
     *
     * @param employeeId
     * @param expense
     * @return
     */
    public Expense submitExpense(int employeeId, Expense expense) {
        // Fetch the employee by ID, throw exception if not found
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        // Fetch the category by ID, throw exception if not found
        Categories category = categoriesRepository.findById(expense.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + expense.getCategory().getId()));

        // Fetch the expense status by ID, throw exception if not found
        ExpenseStatus status = expenseStatusRepository.findById(expense.getStatus().getId())
                .orElseThrow(() -> new EntityNotFoundException("Expense Status not found with ID: " + expense.getStatus().getId()));

        // Associate the expense with the employee, category, and status
        expense.setEmployee(employee);
        expense.setCategory(category);
        expense.setStatus(status);

        // Set the submit date
        expense.setSubmitDate(LocalDateTime.now());

        // Save the expense to the database
        return expenseRepository.save(expense);
    }


}
