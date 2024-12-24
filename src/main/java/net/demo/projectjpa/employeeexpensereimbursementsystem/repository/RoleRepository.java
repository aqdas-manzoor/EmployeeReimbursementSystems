package net.demo.projectjpa.employeeexpensereimbursementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.demo.projectjpa.employeeexpensereimbursementsystem.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
