#!/bin/bash
# Employee Expense Reimbursement System — API test flow
# Prerequisites:
#   - App running: ./mvnw spring-boot:run -s settings-local.xml
#   - MySQL DB seeded via src/main/resources/data.sql (roles, categories, statuses, employee)
# Base URL: http://localhost:8080

# 1. Setup — create category spending package (Travel limit = 5000 for Developer)
curl -X POST http://localhost:8080/manager/category-package \
  -H "Content-Type: application/json" \
  -d '{"category":{"id":1},"packageName":"Dev Travel","expenseLimit":5000}'

# 2. Link role — assign category package to Developer role (role id=1, package id=1)
curl -X POST http://localhost:8080/manager/role-category-package \
  -H "Content-Type: application/json" \
  -d '{"role":{"id":1},"categoryPackage":{"id":1}}'

# 3. Validate — check if expense amount is within role/category limit
curl -X POST http://localhost:8080/manager/employees/validateExpense \
  -H "Content-Type: application/json" \
  -d '{"roleId":1,"categoryPackageId":1,"expenseAmount":450}'

# 4. Submit expense — employee 1 submits Travel expense (category id=1)
curl -X POST http://localhost:8080/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"amount":450,"description":"Taxi to client","category":{"id":1}}'

# 5. View pending — manager lists all pending expenses
curl http://localhost:8080/manager/employees/expenses

# 6. Approve — manager approves expense id=1 (statusId 2 = Approved)
curl -X PATCH "http://localhost:8080/manager/updateStatus?expenseId=1&statusId=2"

# 7. View history — employee 1 spending history and remaining budget per category
curl http://localhost:8080/employees/history/1

# 8. Filter by date — employee 1 expenses between start and end date
curl "http://localhost:8080/employees/expenses/filter?employeeId=1&startDate=2026-01-01&endDate=2026-12-31"
