package net.demo.projectjpa.employeeexpensereimbursementsystem.repository;

import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
}
