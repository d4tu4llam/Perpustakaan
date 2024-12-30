package com.karyaanakbangsa.perpustakaan.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.karyaanakbangsa.perpustakaan.dto.RegistrationDto;
import com.karyaanakbangsa.perpustakaan.dto.UserDto;
import com.karyaanakbangsa.perpustakaan.models.Cart;
import com.karyaanakbangsa.perpustakaan.models.Role;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;
import com.karyaanakbangsa.perpustakaan.repository.CartRepository;
import com.karyaanakbangsa.perpustakaan.repository.RoleRepository;
import com.karyaanakbangsa.perpustakaan.repository.UserRepository;
import com.karyaanakbangsa.perpustakaan.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private CartRepository cartRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, CartRepository cartRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void saveUser(RegistrationDto registrationDto, String roleName) {

        // buat dan isi entity
        UserEntity user = new UserEntity();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // assign role berdasarkan input
        Role role = roleRepository.findByName(roleName.toUpperCase());
        if (role == null) {
            throw new RuntimeException("Role '" + roleName + "' not found");
        }
        user.setRoles(Arrays.asList(role));

        // simpan user
        userRepository.save(user);

        // kalau role USER buat cart
        if (roleName.equalsIgnoreCase("USER")) {
            Cart cart = new Cart();
            cart.setCreatedBy(user); // simpan user sebagai pembuat cart
            cartRepository.save(cart);
        }
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserDto> getAllUser() {
        // ambil users dengan role admin
        List<UserEntity> admins = userRepository.findByRolesName("USER");
        // Map to DTO
        return admins.stream()
                .map(user -> new UserDto(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }
}
