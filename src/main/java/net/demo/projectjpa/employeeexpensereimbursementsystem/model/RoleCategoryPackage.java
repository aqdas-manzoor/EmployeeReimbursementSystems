package net.demo.projectjpa.employeeexpensereimbursementsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role_category_package")
public class RoleCategoryPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;  // Assuming you have a Role entity

    @ManyToOne
    @JoinColumn(name = "category_package_id", nullable = false)
    private CategoryPackage categoryPackage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public CategoryPackage getCategoryPackage() {
        return categoryPackage;
    }

    public void setCategoryPackage(CategoryPackage categoryPackage) {
        this.categoryPackage = categoryPackage;
    }
}
