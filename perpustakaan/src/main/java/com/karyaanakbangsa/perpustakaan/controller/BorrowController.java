package com.karyaanakbangsa.perpustakaan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.karyaanakbangsa.perpustakaan.dto.BorrowDto;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;
import com.karyaanakbangsa.perpustakaan.security.SecurityUtil;
import com.karyaanakbangsa.perpustakaan.service.BorrowService;
import com.karyaanakbangsa.perpustakaan.service.UserService;

import jakarta.validation.Valid;

@Controller
public class BorrowController {

    private final BorrowService borrowService;
    private final UserService userService;

    @Autowired
    public BorrowController(BorrowService borrowService, UserService userService) {
        this.borrowService = borrowService;
        this.userService = userService;

    }

    @GetMapping("/borrows/{userId}")
    public String viewBorrowedBooks(@PathVariable int userId, Model model) {
        List<BorrowDto> borrowedBooks = borrowService.getBorrowedBooks(userId);

        // Menambahkan pesan jika tidak ada buku yang dipinjam
        if (borrowedBooks.isEmpty()) {
            model.addAttribute("message", "Anda belum meminjam buku.");
        }

        model.addAttribute("borrowedBooks", borrowedBooks);

        return "borrows/index";
    }

    @GetMapping("/borrows/{userId}/success")
    public String listBorrows(@PathVariable int userId, Model model) {
        List<BorrowDto> borrows = borrowService.getBorrowedBooks(userId);
        String username = SecurityUtil.getSessionUser(); // Ambil username dari sesi
        if (username != null) {
            UserEntity currentUser = userService.findByUsername(username);
            if (currentUser != null) {
                model.addAttribute("userId", currentUser.getId()); // Tambahkan userId ke model
            }
        }
        model.addAttribute("borrows", borrows);
        return "borrows/success";
    }

    @PostMapping("/borrows/user/return")
    public String returnBooks(@RequestParam(required = false) List<Integer> borrowIds) {
        if (borrowIds == null) {
            return "redirect:/bukus";
        }
        System.out.println("Returning books with borrowIds: " + borrowIds);
        borrowService.returnAndRemoveBooks(borrowIds); // Mengubah status buku dan menghapus entri borrow
        return "redirect:/bukus";
    }

    @GetMapping("/borrows/{borrowId}/edit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String editBorrowForm(@PathVariable("borrowId") Integer borrowId,
            Model model) {
        BorrowDto borrow = borrowService.findBorrowById(borrowId);
        System.out.println("wallahii" + borrow.getUser().getId());
        model.addAttribute("borrow", borrow);
        return "borrows/edit";
    }

    @PostMapping("/borrows/{borrowId}/edit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String editBorrow(@PathVariable("borrowId") Integer borrowId,
            @Valid @ModelAttribute("borrow") BorrowDto borrow,
            BindingResult result) {
        if (result.hasErrors()) {
            return "borrows/edit";
        }
        borrow.setBorrowId(borrowId);
        borrow.setUser(borrow.getUser());
        borrowService.editBorrow(borrow);
        return "redirect:/bukus";
    }
}
