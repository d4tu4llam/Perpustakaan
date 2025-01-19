package com.karyaanakbangsa.perpustakaan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karyaanakbangsa.perpustakaan.models.Role;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByEmail(String email);

    UserEntity findByUsername(String userName);

    UserEntity findFirstByUsername(String username);

    List<UserEntity> findByRole(Role role);

}
