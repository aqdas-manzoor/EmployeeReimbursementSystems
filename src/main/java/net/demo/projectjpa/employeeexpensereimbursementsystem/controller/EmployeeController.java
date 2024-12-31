package net.demo.projectjpa.employeeexpensereimbursementsystem.controller;

import jakarta.persistence.EntityNotFoundException;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Expense;
import net.demo.projectjpa.employeeexpensereimbursementsystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {


    private final EmployeeService employeeService;


    @Autowired
    public EmployeeController(EmployeeService expenseService, EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * employee submit the expense request using his Id
     * @param employeeId
     * @param expense
     * @return
     */

    @PostMapping("/{employeeId}")
    public ResponseEntity<?> submitExpense(@PathVariable int employeeId, @RequestBody Expense expense) {
        try {
            // Call the service layer to handle business logic
            Expense savedExpense = employeeService.submitExpense(employeeId, expense);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense); // Use 201 Created for new resources
        } catch (EntityNotFoundException ex) {
            // Return a meaningful error message for entity not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            // Handle unexpected exceptions with a detailed error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + ex.getMessage());
        }
    }

    /**
     * Endpoint to get expenses by employee ID and date range.
     *
     * @param employeeId the ID of the employee
     * @param startDate  the start date of the range
     * @param endDate    the end date of the range
     * @return a list of expenses within the date range for the given employee
     */
    @GetMapping("/expenses/filter")
    public ResponseEntity<List<Expense>> getExpensesByEmployeeAndDateRange(
            @RequestParam int employeeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        // Parse the date strings to LocalDate
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // Get the filtered expenses
        List<Expense> expenses = employeeService.getExpensesByEmployeeIdAndDateRange(employeeId, start, end);

        // Return the expenses as the response
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get the history of submitted expenses and remaining amount by category for a given employee
     *
     * @param employeeId the ID of the employee
     * @return list of category history with remaining amount
     */
    @GetMapping("/history/{employeeId}")
    public List<Map<String, Object>> getEmployeeHistoryByCategory(@PathVariable int employeeId) {
        return employeeService.getEmployeeHistoryByCategory(employeeId);
    }

}