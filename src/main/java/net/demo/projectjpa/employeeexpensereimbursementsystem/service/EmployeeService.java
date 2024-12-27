package net.demo.projectjpa.employeeexpensereimbursementsystem.service;

import jakarta.persistence.EntityNotFoundException;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.*;
import net.demo.projectjpa.employeeexpensereimbursementsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {


    private final ExpenseRepository expenseRepository;
    private final ExpenseStatusRepository expenseStatusRepository;
    private final CategoriesRepository categoriesRepository;
    private final EmployeeRepository employeeRepository;
    private final CategoryPackageRepository categoryPackageRepository;
    private final RoleCategoryPackageRepository roleCategoryPackageRepository;

    @Autowired
    public EmployeeService(ExpenseRepository expenseRepository, ExpenseStatusRepository expenseStatusRepository, CategoriesRepository categoriesRepository, EmployeeRepository employeeRepository, CategoryPackageRepository categoryPackageRepository, RoleCategoryPackageRepository roleCategoryPackageRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseStatusRepository = expenseStatusRepository;
        this.categoriesRepository = categoriesRepository;
        this.employeeRepository = employeeRepository;
        this.categoryPackageRepository = categoryPackageRepository;
        this.roleCategoryPackageRepository = roleCategoryPackageRepository;
    }

    /**
     * Submits a new expense for an employee by associating it with the employee, category, and status.
     * It fetches the employee, category, and status by their IDs, throwing an {@link EntityNotFoundException}
     * if any are not found. The expense is then saved with the current submit date.
     *
     * @param employeeId
     * @param expense
     * @return
     */
    public Expense submitExpense(int employeeId, Expense expense) {
        // Fetch the employee by ID, throw exception if not found
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        // Validate the employee's role status (assuming 'status' is a boolean)
        if (!employee.getRole().getStatus()) {
            throw new IllegalStateException("Employee role is inactive. Cannot submit expense.");
        }

        // Fetch the category by ID, throw exception if not found
        Categories category = categoriesRepository.findById(expense.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + expense.getCategory().getId()));

        // Fetch the default expense status by hardcoded ID (e.g., 1), throw exception if not found
        ExpenseStatus status = expenseStatusRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Expense Status not found with ID: 1"));

        // Associate the expense with the employee, category, and status
        expense.setEmployee(employee);
        expense.setCategory(category);
        expense.setStatus(status);

        // Set the submit date
        expense.setSubmitDate(LocalDateTime.now());

        // Set approved date to null by default
        expense.setApprovedDate(null);

        // Save the expense to the database
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllEmployeesWithExpenses() {
        // Fetch all employees from the repository
        List<Expense> expense = expenseRepository.findAll();
        return expense;
    }

    /**
     * Retrieves expenses for all employees filtered by the given expense status.
     *
     * @param statusName the name of the expense status (e.g., "Approved", "Pending")
     * @return a list of expenses with the given status
     */
    public List<Expense> getExpensesByStatus(String statusName) {
        // Fetch all statuses by the provided name
        List<ExpenseStatus> statuses = expenseStatusRepository.findByName(statusName);

        // If no statuses are found, throw an exception
        if (statuses.isEmpty()) {
            throw new EntityNotFoundException("Expense Status not found with name: " + statusName);
        }

        // Create a list to store expenses that match any of the found statuses
        List<Expense> allExpenses = new ArrayList<>();

        // Iterate through all the statuses and fetch the expenses for each status
        for (ExpenseStatus status : statuses) {
            // For each status, find all the expenses associated with it
            List<Expense> expenses = expenseRepository.findByStatus(status);

            // Add the found expenses to the list
            allExpenses.addAll(expenses);
        }

        // Return the list of expenses that match any of the statuses
        return allExpenses;
    }

    /**
     * Updates the status of an expense by its id.
     * If the status is "approved" (ID 2), the approved date is set to the current date.
     * If the status is "rejected" (ID 3), the approved date is set to null.
     *
     * @param expenseId the id of the expense to update
     * @param newStatus the new status to set for the expense
     * @return the updated expense
     * @throws EntityNotFoundException  if the expense or status is not found
     * @throws IllegalArgumentException if the status ID is invalid
     */
    public Expense updateExpenseStatus(int expenseId, ExpenseStatus newStatus) {
        // Fetch the expense by ID, throw exception if not found
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with ID: " + expenseId));

        // Fetch the new status by ID, throw exception if not found
        ExpenseStatus status = expenseStatusRepository.findById(newStatus.getId())
                .orElseThrow(() -> new EntityNotFoundException("Expense Status not found with ID: " + newStatus.getId()));

        // Check if the status ID is valid (1: Pending, 2: Approved, 3: Rejected)
        if (newStatus.getId() < 1 || newStatus.getId() > 3) {
            throw new IllegalArgumentException("Invalid status ID. Allowed values are 1 (Pending), 2 (Approved), or 3 (Rejected).");
        }

        // If the status is "approved" (ID 2), set the approvedDate to the current date
        if (status.getId() == 2) {
            expense.setApprovedDate(LocalDateTime.now());
        } else if (status.getId() == 3) {
            // Leave the approvedDate as null for rejected status
            expense.setApprovedDate(null);
        } else {
            // If status is "pending" (ID 1), do nothing to the approvedDate
            expense.setApprovedDate(null);
        }

        // Set the new status
        expense.setStatus(status);

        // Save the updated expense
        return expenseRepository.save(expense);
    }

    public boolean validateExpenseLimit(int roleId, int categoryPackageId, int expenseAmount) {
        Optional<RoleCategoryPackage> roleCategoryPackage = roleCategoryPackageRepository.findById(categoryPackageId);
        if (roleCategoryPackage.isPresent() &&
                roleCategoryPackage.get().getRole().getId() == roleId) {
            return expenseAmount <= roleCategoryPackage.get().getCategoryPackage().getExpenseLimit();
        }
        return false;
    }


    /**
     * Get expenses by employeeId and a date range (only considering the date portion).
     *
     * @param employeeId the ID of the employee
     * @param startDate  the start date of the range (in LocalDate format)
     * @param endDate    the end date of the range (in LocalDate format)
     * @return a list of expenses within the date range for the given employee
     */
    public List<Expense> getExpensesByEmployeeIdAndDateRange(int employeeId, LocalDate startDate, LocalDate endDate) {
        // Convert LocalDate to LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay();  // Set time to 00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 999999999);  // Set time to 23:59:59.999
        // Use the repository to fetch data
        return expenseRepository.findByEmployeeIdAndDateRange(employeeId, startDateTime, endDateTime);
    }
}