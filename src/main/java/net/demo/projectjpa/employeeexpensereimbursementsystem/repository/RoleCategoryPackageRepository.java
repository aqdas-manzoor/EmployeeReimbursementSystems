package net.demo.projectjpa.employeeexpensereimbursementsystem.repository;

import net.demo.projectjpa.employeeexpensereimbursementsystem.model.RoleCategoryPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RoleCategoryPackageRepository extends JpaRepository<RoleCategoryPackage, Integer> {
    List<RoleCategoryPackage> findByRoleId(int roleId);
}