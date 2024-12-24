package net.demo.projectjpa.employeeexpensereimbursementsystem.controller;

import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Expense;
import net.demo.projectjpa.employeeexpensereimbursementsystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {


    private final EmployeeService expenseService;


    @Autowired
    public EmployeeController(EmployeeService expenseService) {
        this.expenseService = expenseService;
    }

@PostMapping("/{employeeId}")
public ResponseEntity<Expense> submitExpense(@PathVariable int employeeId,@RequestBody Expense expense) {
    // Call the service layer to handle business logic
    Expense savedExpense = expenseService.submitExpense(employeeId,expense);

    return ResponseEntity.ok(savedExpense);
}


}
