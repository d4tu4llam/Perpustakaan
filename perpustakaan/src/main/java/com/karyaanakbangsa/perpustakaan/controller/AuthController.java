package com.karyaanakbangsa.perpustakaan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.karyaanakbangsa.perpustakaan.dto.RegistrationDto;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;
import com.karyaanakbangsa.perpustakaan.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {
    private UserService userService;

    private static final String ADMIN_CODE = "SECRET123";

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        RegistrationDto user = new RegistrationDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String register(@Valid @ModelAttribute("user") RegistrationDto user,
            BindingResult result, Model model) {
        // Validasi jika email sudah digunakan
        UserEntity existingUserEmail = userService.findByEmail(user.getEmail());
        if (existingUserEmail != null) {
            return "redirect:/register?fail";
        }
        // Validasi jika username sudah digunakan
        UserEntity existingUserUsername = userService.findByUsername(user.getUsername());
        if (existingUserUsername != null) {
            return "redirect:/register?fail";
        }
        // Validasi jika ada error dari input user
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        // Validasi kode admin hanya jika role adalah ADMIN
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            if (!ADMIN_CODE.equals(user.getAdminCode())) {
                model.addAttribute("user", user);
                model.addAttribute("adminCodeError", "Invalid admin code");
                return "register";
            }
            try {
                userService.saveAdmin(user, user.getRole());
                return "redirect:/login?success";
            } catch (Exception e) {
                return "redirect:/register?fail";
            }
        }

        // Simpan user
        try {
            userService.saveMember(user, user.getRole());
            return "redirect:/login?success";
        } catch (Exception e) {
            return "redirect:/register?fail";
        }
    }

}
