package net.demo.projectjpa.employeeexpensereimbursementsystem.controller;

import jakarta.persistence.EntityNotFoundException;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Expense;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.ExpenseStatus;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.ExpenseValidationRequest;
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
    // Get only pending status expenses
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
     * @param statusName the status of the expenses (e.g., "Pending", "Approved")
     * @return a list of expenses with the given status
     */
    @GetMapping("/employees/expenses/status")
    public ResponseEntity<List<Expense>> getExpensesByStatus(@RequestParam String statusName) {
        // Call the service to retrieve expenses filtered by status
        List<Expense> expenses = employeeService.getExpensesByStatus(statusName);

        // Return the list of expenses with the given status
        return ResponseEntity.ok(expenses);
    }

    /**
     * PATCH endpoint to update the status of an expense by id
     *
     * @param expenseId
     * @param newStatus
     * @return
     */
    @PatchMapping("/employee/{expenseId}/status")
    public ResponseEntity<Expense> updateExpenseStatus(@PathVariable int expenseId, @RequestBody ExpenseStatus newStatus) {
        try {
            // Call the service layer to update the status
            Expense updatedExpense = employeeService.updateExpenseStatus(expenseId, newStatus);
            return ResponseEntity.ok(updatedExpense);
        } catch (EntityNotFoundException ex) {
            // Handle cases where the entity (Expense or Status) is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            // Handle any other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("employees/validateExpense")
    public ResponseEntity<Boolean> validateExpense(@RequestBody ExpenseValidationRequest request) {
        boolean isValid = employeeService.validateExpenseLimit(request.getRoleId(), request.getCategoryPackageId(), request.getExpenseAmount());
        return ResponseEntity.ok(isValid);
    }
}

