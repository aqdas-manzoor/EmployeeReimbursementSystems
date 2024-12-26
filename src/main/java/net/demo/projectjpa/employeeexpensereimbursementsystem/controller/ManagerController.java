package net.demo.projectjpa.employeeexpensereimbursementsystem.controller;

import jakarta.persistence.EntityNotFoundException;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Employee;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Expense;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.ExpenseStatus;
import net.demo.projectjpa.employeeexpensereimbursementsystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
public class ManagerController {


    private final EmployeeService employeeService;

    @Autowired
    public ManagerController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Retrieves all employees along with their expense details.
     *
     * @return a list of objects containing employee and expense information
     */
    @GetMapping("/employees/expenses")
    public ResponseEntity<List<Expense>> getAllEmployees() {
        // Call the service to retrieve all employees with their expense details
        List<Expense> expenses = employeeService.getAllEmployeesWithExpenses();

        // Return the list of employees along with their expenses
        return ResponseEntity.ok(expenses);
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
}
