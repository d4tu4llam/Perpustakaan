package com.karyaanakbangsa.perpustakaan.service;

import java.util.List;

import com.karyaanakbangsa.perpustakaan.dto.BukuDto;
import com.karyaanakbangsa.perpustakaan.dto.CartDto;
import com.karyaanakbangsa.perpustakaan.dto.UserCartDto;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;

public interface CartService {

    // Mengambil cart berdasarkan ID
    CartDto getCart();

    // Menambahkan buku ke dalam cart dengan jumlah tertentu
    void addToCart(int bukuId);

    // Menghapus buku dari cart
    void removeFromCart(int bukuId);

    List<UserEntity> getUsersWithCarts();

    List<BukuDto> getCartItemsByUserId(int userId);

    List<UserCartDto> getAllCartsForAdmin();

    void checkoutBooks(int userId, List<Integer> bukuIds);

}
