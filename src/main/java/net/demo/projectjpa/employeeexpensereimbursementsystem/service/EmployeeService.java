package net.demo.projectjpa.employeeexpensereimbursementsystem.service;

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


//    public Expense submitExpense(int employeeId,Expense expense) {
//        // Fetch the employee by ID
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
//
//        // Associate the expense with the employee
//        expense.setEmployee(employee);
//
//        // Set the submit date
//        expense.setSubmitDate(LocalDateTime.now());
//
//        // Save the expense
//        return expenseRepository.save(expense);
//    }
public Expense submitExpense(int employeeId, Expense expense) {
    // Fetch the employee by ID
    Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

    // Fetch the category by ID
    Categories category = categoriesRepository.findById(expense.getCategory().getId())
            .orElseThrow(() -> new RuntimeException("Category not found with ID: " + expense.getCategory().getId()));

    // Fetch the status by ID
    ExpenseStatus status = expenseStatusRepository.findById(expense.getStatus().getId())
            .orElseThrow(() -> new RuntimeException("Expense Status not found with ID: " + expense.getStatus().getId()));

    // Associate the expense with the employee, category, and status
    expense.setEmployee(employee);
    expense.setCategory(category);
    expense.setStatus(status);

    // Set the submit date
    expense.setSubmitDate(LocalDateTime.now());

    // Save the expense
    return expenseRepository.save(expense);
}


}
