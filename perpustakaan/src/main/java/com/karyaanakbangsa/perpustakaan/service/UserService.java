package com.karyaanakbangsa.perpustakaan.service;

import java.util.List;

import com.karyaanakbangsa.perpustakaan.dto.RegistrationDto;
import com.karyaanakbangsa.perpustakaan.dto.UserDto;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;

public interface UserService {
    void saveUser(RegistrationDto registrationDto, String roleName);

    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);

    List<UserDto> getAllUser();
}
