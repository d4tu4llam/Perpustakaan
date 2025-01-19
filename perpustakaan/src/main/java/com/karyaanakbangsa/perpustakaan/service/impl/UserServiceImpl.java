package com.karyaanakbangsa.perpustakaan.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.karyaanakbangsa.perpustakaan.dto.RegistrationDto;
import com.karyaanakbangsa.perpustakaan.dto.UserDto;
import com.karyaanakbangsa.perpustakaan.models.Admin;
import com.karyaanakbangsa.perpustakaan.models.Cart;
import com.karyaanakbangsa.perpustakaan.models.Member;
import com.karyaanakbangsa.perpustakaan.models.Role;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;
import com.karyaanakbangsa.perpustakaan.repository.CartRepository;
import com.karyaanakbangsa.perpustakaan.repository.MemberRepository;
import com.karyaanakbangsa.perpustakaan.repository.RoleRepository;
import com.karyaanakbangsa.perpustakaan.repository.UserRepository;
import com.karyaanakbangsa.perpustakaan.repository.AdminRepository;
import com.karyaanakbangsa.perpustakaan.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private CartRepository cartRepository;
    private PasswordEncoder passwordEncoder;
    private MemberRepository MemberRepository;
    private AdminRepository AdminRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, CartRepository cartRepository,
            PasswordEncoder passwordEncoder, MemberRepository MemberRepository, AdminRepository AdminRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.MemberRepository = MemberRepository;
        this.AdminRepository = AdminRepository;
    }

    @Override
    @Transactional
    public void saveMember(RegistrationDto registrationDto, String roleName) {

        // buat dan isi entity
        Member member = new Member();
        member.setUsername(registrationDto.getUsername());
        member.setEmail(registrationDto.getEmail());
        member.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        member.setNoTelepon(registrationDto.getNoTelepon());
        member.setRole(roleRepository.findByRole(roleName).get());
        // assign role berdasarkan input
        Role role = roleRepository.findFirstByRole(roleName.toUpperCase());
        if (role == null) {
            throw new RuntimeException("Role '" + roleName + "' not found");
        }
        member.setRole((role));

        // simpan user
        MemberRepository.save(member);

        // kalau role USER buat cart
        if (roleName.equalsIgnoreCase("USER")) {
            Cart cart = new Cart();
            cart.setCreatedBy(member); // simpan user sebagai pembuat cart
            cartRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public void saveAdmin(RegistrationDto registrationDto, String roleName) {

        // buat dan isi entity
        Admin admin = new Admin();
        admin.setUsername(registrationDto.getUsername());
        admin.setEmail(registrationDto.getEmail());
        admin.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        admin.setNip(registrationDto.getNip());
        admin.setRole(roleRepository.findByRole(roleName).get());
        // assign role berdasarkan input
        Role role = roleRepository.findFirstByRole(roleName.toUpperCase());
        if (role == null) {
            throw new RuntimeException("Role '" + roleName + "' not found");
        }
        admin.setRole((role));

        // simpan user
        AdminRepository.save(admin);

        // kalau role USER buat cart
        if (roleName.equalsIgnoreCase("USER")) {
            Cart cart = new Cart();
            cart.setCreatedBy(admin); // simpan user sebagai pembuat cart
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
        List<UserEntity> admins = userRepository.findByRole(roleRepository.findByRole("USER").get());
        // Map to DTO
        return admins.stream()
                .map(user -> new UserDto(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }
}
