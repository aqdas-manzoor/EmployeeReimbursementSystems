package net.demo.projectjpa.employeeexpensereimbursementsystem.service;

import jakarta.persistence.EntityNotFoundException;
import net.demo.projectjpa.employeeexpensereimbursementsystem.model.*;
import net.demo.projectjpa.employeeexpensereimbursementsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        if (!employee.getRole().getStatus()) {
            throw new IllegalStateException("Employee role is inactive. Cannot submit expense.");
        }

        Categories category = categoriesRepository.findById(expense.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + expense.getCategory().getId()));

        ExpenseStatus status = expenseStatusRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Expense Status not found with ID: 1"));

        // Associate the expense with the employee, category, and status
        expense.setEmployee(employee);
        expense.setCategory(category);
        expense.setStatus(status);

        expense.setSubmitDate(LocalDateTime.now());

        expense.setApprovedDate(null);

        return expenseRepository.save(expense);
    }
    public CategoryPackage createCategoryPackage(CategoryPackage categoryPackage) {
        return categoryPackageRepository.save(categoryPackage);
    }

    public RoleCategoryPackage saveRoleCategoryPackage(RoleCategoryPackage roleCategoryPackage) {
        return roleCategoryPackageRepository.save(roleCategoryPackage);
    }

    public List<Expense> getAllEmployeesWithExpenses() {
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

        if (statuses.isEmpty()) {
            throw new EntityNotFoundException("Expense Status not found with name: " + statusName);
        }

        List<Expense> allExpenses = new ArrayList<>();

        for (ExpenseStatus status : statuses) {
            List<Expense> expenses = expenseRepository.findByStatus(status);

            allExpenses.addAll(expenses);
        }

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
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with ID: " + expenseId));

        ExpenseStatus status = expenseStatusRepository.findById(newStatus.getId())
                .orElseThrow(() -> new EntityNotFoundException("Expense Status not found with ID: " + newStatus.getId()));
        if (newStatus.getId() < 1 || newStatus.getId() > 3) {
            throw new IllegalArgumentException("Invalid status ID. Allowed values are 1 (Pending), 2 (Approved), or 3 (Rejected).");
        }

        if (status.getId() == 2) {
            expense.setApprovedDate(LocalDateTime.now());
        } else if (status.getId() == 3) {
            expense.setApprovedDate(null);
        } else {
            expense.setApprovedDate(null);
        }

        expense.setStatus(status);
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
        LocalDateTime startDateTime = startDate.atStartOfDay();  // Set time to 00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 999999999);  // Set time to 23:59:59.999
        return expenseRepository.findByEmployeeIdAndDateRange(employeeId, startDateTime, endDateTime);
    }

    /**
     * Get expense history and remaining amount by category for the given employee
     *
     * @param employeeId the ID of the employee
     * @return list of categories with history and remaining amounts
     * Fetch the employee by ID, Fetch the employee's role,
     * Fetch role-based category packages using the role's ID
     * Fetch all expenses for the employee , Group expenses by category ID and calculate the total submitted amount for each category
     */
    public List<Map<String, Object>> getEmployeeHistoryByCategory(int employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        Role role = employee.getRole();

        List<RoleCategoryPackage> roleCategoryPackages = roleCategoryPackageRepository.findByRoleId(role.getId());

        List<Expense> expenses = expenseRepository.findByEmployeeId(employeeId);
        Map<Integer, Integer> categoryExpenseMap = expenses.stream()
                .collect(Collectors.groupingBy(expense -> expense.getCategory().getId(),
                        Collectors.summingInt(Expense::getAmount)));

        return roleCategoryPackages.stream().map(roleCategoryPackage -> {
            CategoryPackage categoryPackage = roleCategoryPackage.getCategoryPackage();
            int categoryId = categoryPackage.getCategory().getId();
            String categoryName = categoryPackage.getCategory().getName();
            int expenseLimit = categoryPackage.getExpenseLimit();

            int totalSubmittedAmount = categoryExpenseMap.getOrDefault(categoryId, 0);

            int remainingAmount = expenseLimit - totalSubmittedAmount;

            Map<String, Object> categoryHistory = new HashMap<>();
            categoryHistory.put("categoryName", categoryName);
            categoryHistory.put("totalSubmittedAmount", totalSubmittedAmount);
            categoryHistory.put("remainingAmount", remainingAmount);

            if (remainingAmount < 0) {
                categoryHistory.put("message", "You have exceeded the expense limit for the " + categoryName + " category.");
            }

            return categoryHistory;
        }).collect(Collectors.toList());
    }
}