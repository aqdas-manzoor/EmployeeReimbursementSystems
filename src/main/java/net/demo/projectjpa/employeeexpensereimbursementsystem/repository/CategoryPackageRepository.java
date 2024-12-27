package net.demo.projectjpa.employeeexpensereimbursementsystem.repository;

import net.demo.projectjpa.employeeexpensereimbursementsystem.model.CategoryPackage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryPackageRepository extends JpaRepository<CategoryPackage, Integer> {

    }
