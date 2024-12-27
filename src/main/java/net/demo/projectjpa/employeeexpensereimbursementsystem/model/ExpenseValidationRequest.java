package net.demo.projectjpa.employeeexpensereimbursementsystem.model;

public class ExpenseValidationRequest {
    private int roleId;
    private int categoryPackageId;
    private int expenseAmount;
    public int getRoleId() {
        return roleId;
    }
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
    public int getCategoryPackageId() {
        return categoryPackageId;
    }
    public void setCategoryPackageId(int categoryPackageId) {
        this.categoryPackageId = categoryPackageId;
    }
    public int getExpenseAmount() {
        return expenseAmount;
    }
    public void setExpenseAmount(Integer expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

}
