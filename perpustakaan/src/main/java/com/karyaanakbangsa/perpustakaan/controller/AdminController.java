package com.karyaanakbangsa.perpustakaan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.karyaanakbangsa.perpustakaan.dto.BukuDto;
import com.karyaanakbangsa.perpustakaan.dto.UserCartDto;
import com.karyaanakbangsa.perpustakaan.dto.UserDto;
import com.karyaanakbangsa.perpustakaan.service.CartService;
import com.karyaanakbangsa.perpustakaan.service.UserService;

@Controller
public class AdminController {
    private CartService cartService;
    private UserService userService;

    @Autowired
    public AdminController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/checkouts")
    public String viewAllCarts(Model model) {
        List<UserCartDto> userCarts = cartService.getAllCartsForAdmin();
        model.addAttribute("userCarts", userCarts);
        return "checkouts/cart"; // Nama template HTML
    }

    @GetMapping("/borrows")
    public String viewAllBorrow(Model model) {
        List<UserDto> Users = userService.getAllUser();
        model.addAttribute("users", Users);
        return "borrows/list"; // Return the template for displaying admins
    }

    @GetMapping("/checkouts/cart/{userId}")
    public String viewCartByUser(@PathVariable int userId, Model model) {
        List<BukuDto> bukuDtos = cartService.getCartItemsByUserId(userId);
        model.addAttribute("bukuDtos", bukuDtos);
        return "checkouts/usercart"; // Nama template HTML
    }

    @PostMapping("/admin/checkout")
    public String checkoutBuku(
            @RequestParam("userId") int userId,
            @RequestParam(value = "bukuIds", required = false) List<Integer> bukuIds) {
        if (bukuIds == null) {
            return "redirect:/checkouts/cart/" + userId; // Redirect kembali ke halaman cart
        }
        cartService.checkoutBooks(userId, bukuIds);
        return "redirect:/checkouts/cart/" + userId; // Redirect kembali ke halaman cart
    }

}

// /checkouts/cart/{userId}(userId=${userCart.userId}