package com.karyaanakbangsa.perpustakaan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karyaanakbangsa.perpustakaan.models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRole(String role);

    Role findFirstByRole(String role);
}