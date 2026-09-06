-- =============================================================================
-- Employee Expense Reimbursement System — Database setup from scratch
-- =============================================================================
--
-- SOFTWARE PREREQUISITES
--   - Java JDK 23
--   - MySQL 8.x running on localhost:3306
--   - Maven (or use ./mvnw in project root)
--
-- APPLICATION CONFIG (application.properties)
--   spring.datasource.url=jdbc:mysql://localhost:3306/expense_reimbursement_system
--   spring.datasource.username=root
--   spring.datasource.password=<your-password>
--
-- =============================================================================
-- STEP 1: Create empty database
-- =============================================================================
-- Tables are created automatically by Hibernate on first app start
-- (spring.jpa.hibernate.ddl-auto=update). You only need an empty database.

CREATE DATABASE IF NOT EXISTS expense_reimbursement_system;
USE expense_reimbursement_system;

-- =============================================================================
-- STEP 2: Start the app once to create tables
-- =============================================================================
--   cd EmployeeReimbursementSystems
--   ./mvnw spring-boot:run -s settings-local.xml
--
-- Hibernate creates these tables automatically:
--   role, categories, expense_status, employee, expense,
--   category_package, role_category_package
--
-- Static seed data is loaded automatically from src/main/resources/data.sql
-- on startup. If you prefer manual insert, stop the app and run STEP 3 below.
--
-- =============================================================================
-- STEP 3: Static tables — insert manually (only if NOT using data.sql)
-- =============================================================================
-- Order matters because of foreign keys:
--   role → categories → expense_status → employee
--   → category_package → role_category_package
-- expense table is EMPTY at start (filled via API when employees submit claims)

-- -----------------------------------------------------------------------------
-- TABLE: role (required)
-- Used by: employee.role_id, role_category_package.role_id
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO role (id, name, status) VALUES
    (1, 'Developer', true),
    (2, 'Manager', true);

-- -----------------------------------------------------------------------------
-- TABLE: categories (required)
-- Used by: expense.category_id, category_package.category_id
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO categories (id, name, status) VALUES
    (1, 'Travel', true),
    (2, 'Food', true),
    (3, 'Equipment', true);

-- -----------------------------------------------------------------------------
-- TABLE: expense_status (required)
-- Used by: expense.status_id
-- App hardcodes: 1=Pending (on submit), 2=Approved, 3=Rejected
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO expense_status (id, name, status) VALUES
    (1, 'Pending', true),
    (2, 'Approved', true),
    (3, 'Rejected', true);

-- -----------------------------------------------------------------------------
-- TABLE: employee (required — at least one employee to test)
-- Used by: expense.employee_id
-- role_id must reference an existing role
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO employee (id, name, email, role_id) VALUES
    (1, 'John Doe', 'john@company.com', 1);

-- -----------------------------------------------------------------------------
-- TABLE: category_package (recommended for validation & history APIs)
-- Defines spending limit per category
-- category_id must reference categories.id
-- Can also be created via: POST /manager/category-package
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO category_package (id, category_id, package_name, expense_limit) VALUES
    (1, 1, 'Dev Travel', 5000),
    (2, 2, 'Dev Food', 2000),
    (3, 3, 'Dev Equipment', 3000);

-- -----------------------------------------------------------------------------
-- TABLE: role_category_package (recommended for validation & history APIs)
-- Links a role to allowed category packages / limits
-- role_id → role.id, category_package_id → category_package.id
-- Can also be created via: POST /manager/role-category-package
-- NOTE: validateExpense uses THIS table's id as categoryPackageId (not category_package.id)
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO role_category_package (id, role_id, category_package_id) VALUES
    (1, 1, 1),
    (2, 1, 2),
    (3, 1, 3);

-- =============================================================================
-- TABLE: expense (dynamic — do NOT seed manually for normal testing)
-- Created when employee submits via: POST /employees/{employeeId}
-- =============================================================================

-- =============================================================================
-- STATIC DATA REFERENCE (IDs used in test curls)
-- =============================================================================
--
-- role
--   1 = Developer    2 = Manager
--
-- categories
--   1 = Travel       2 = Food       3 = Equipment
--
-- expense_status
--   1 = Pending      2 = Approved     3 = Rejected
--
-- employee
--   1 = John Doe (Developer, john@company.com)
--
-- category_package
--   1 = Dev Travel (5000)   2 = Dev Food (2000)   3 = Dev Equipment (3000)
--
-- role_category_package
--   1 = Developer + Dev Travel   (use id=1 in validateExpense)
--   2 = Developer + Dev Food
--   3 = Developer + Dev Equipment
--
-- =============================================================================
-- STEP 4: Start app and test
-- =============================================================================
--   ./mvnw spring-boot:run -s settings-local.xml
--   bash scripts/test-api-flow.sh
--   Swagger UI: http://localhost:8080/swagger-ui.html
