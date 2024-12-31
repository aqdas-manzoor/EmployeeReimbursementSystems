package net.demo.projectjpa.employeeexpensereimbursementsystem.controller;

import jakarta.persistence.EntityNotFoundException;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.*;
import net.demo.projectjpa.employeeexpensereimbursementsystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manager")
public class ManagerController {


    private final EmployeeService employeeService;

    @Autowired
    public ManagerController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Retrieves all employees along with their pending expense details.
     *
     * @return a list of objects containing employee and expense information
     */
    @GetMapping("/employees/expenses")
    public ResponseEntity<List<Expense>> getPendingExpenses() {
        List<Expense> pendingExpenses = employeeService.getAllEmployeesWithExpenses().stream()
                .filter(expense -> expense.getStatus() != null && "Pending".equalsIgnoreCase(expense.getStatus().getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingExpenses);
    }


    /**
     * Retrieves all expenses for employees filtered by the given status.
     *
     * @param statusId the status of the expenses (e.g., "Pending", "Approved")
     * @return a list of expenses with the given status
     */
    @GetMapping("/employees/expenses/status")
    public ResponseEntity<List<Expense>> getExpensesByStatus(@RequestParam int statusId) {
        List<Expense> expenses = employeeService.getExpensesByStatus(statusId);

        return ResponseEntity.ok(expenses);
    }

    /**
     * PATCH endpoint to update the status of an expense by id
     *
     * @param expenseId
     * @param statusId
     * @return
     */
    @PatchMapping("/updateStatus")
    public ResponseEntity<Expense> updateExpenseStatus(@RequestParam int expenseId, @RequestParam int statusId) {
        try {
            Expense updatedExpense = employeeService.updateExpenseStatus(expenseId, statusId);
            return ResponseEntity.ok(updatedExpense);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("employees/validateExpense")
    public ResponseEntity<Boolean> validateExpense(@RequestBody ExpenseValidationRequest request) {
        boolean isValid = employeeService.validateExpenseLimit(request.getRoleId(), request.getCategoryPackageId(), request.getExpenseAmount());
        return ResponseEntity.ok(isValid);
    }


    @PostMapping("/category-package")
    public ResponseEntity<CategoryPackage> createCategoryPackage(@RequestBody CategoryPackage categoryPackage) {
        CategoryPackage createdCategoryPackage = employeeService.createCategoryPackage(categoryPackage);
        return ResponseEntity.ok(createdCategoryPackage);
    }

    @PostMapping("/role-category-package")
    public ResponseEntity<RoleCategoryPackage> createRoleCategoryPackage(@RequestBody RoleCategoryPackage roleCategoryPackage) {
        RoleCategoryPackage savedRoleCategoryPackage = employeeService.saveRoleCategoryPackage(roleCategoryPackage);
        return ResponseEntity.ok(savedRoleCategoryPackage);
    }
}