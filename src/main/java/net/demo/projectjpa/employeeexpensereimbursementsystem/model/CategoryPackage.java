package net.demo.projectjpa.employeeexpensereimbursementsystem.model;

import jakarta.persistence.*;


@Entity
@Table(name = "category_package")
public class CategoryPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Categories category; // Assuming you have a Categories entity

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "expense_limit", nullable = false)
    private int expenseLimit;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getExpenseLimit() {
        return expenseLimit;
    }

    public void setExpenseLimit(int expenseLimit) {
        this.expenseLimit = expenseLimit;
    }
}
