package com.karyaanakbangsa.perpustakaan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karyaanakbangsa.perpustakaan.models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}