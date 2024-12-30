package com.karyaanakbangsa.perpustakaan.controller;

import com.karyaanakbangsa.perpustakaan.dto.CartDto;
import com.karyaanakbangsa.perpustakaan.service.CartService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/carts")
    public String viewCart(Model model) {
        CartDto cartDto = cartService.getCart();
        model.addAttribute("cartItems", cartDto.getBukuDtos());
        return "carts/index";
    }

    @GetMapping("/carts/add/{bukuId}")
    @PreAuthorize("hasAuthority('USER')")
    public String addToCart(@PathVariable("bukuId") int bukuId, RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(bukuId);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/carts";
    }

    @GetMapping("/carts/remove/{bukuId}")
    @PreAuthorize("hasAuthority('USER')")
    public String removeFromCart(@PathVariable("bukuId") int bukuId) {
        cartService.removeFromCart(bukuId);
        return "redirect:/carts";
    }

}
