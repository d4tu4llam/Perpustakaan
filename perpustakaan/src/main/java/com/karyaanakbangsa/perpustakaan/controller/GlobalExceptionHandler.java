package com.karyaanakbangsa.perpustakaan.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Menangani semua error umum
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Terjadi kesalahan yang tidak terduga. Silakan coba lagi.");
        model.addAttribute("errorDetails", ex.getMessage());
        System.out.println("Error Message: " + ex.getMessage());
        return "index"; // Halaman error umum
    }

    // Menangani kesalahan saat parameter tipe tidak sesuai
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
        model.addAttribute("errorMessage", "Parameter yang dimasukkan tidak valid:" + ex.getValue());
        model.addAttribute("errorDetails", ex.getMessage());
        System.out.println("Error Message: " + ex.getMessage());
        return "index"; // Halaman error umum
    }

    // Menangani NoSuchElementException (data tidak ditemukan)
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoSuchElement(NoSuchElementException ex, Model model) {
        model.addAttribute("errorMessage", "Data yang diminta tidak ditemukan.");
        model.addAttribute("errorDetails", ex.getMessage());
        System.out.println("Error Message: " + ex.getMessage());

        return "index"; // Halaman error umum
    }
}
