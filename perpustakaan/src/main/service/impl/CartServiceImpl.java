package com.karyaanakbangsa.perpustakaan.service.impl;

import com.karyaanakbangsa.perpustakaan.dto.CartDto;
import com.karyaanakbangsa.perpustakaan.dto.UserCartDto;
import com.karyaanakbangsa.perpustakaan.dto.BukuDto;
import com.karyaanakbangsa.perpustakaan.models.Cart;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;
import com.karyaanakbangsa.perpustakaan.models.Borrow;
import com.karyaanakbangsa.perpustakaan.models.Buku;
import com.karyaanakbangsa.perpustakaan.repository.CartRepository;
import com.karyaanakbangsa.perpustakaan.repository.UserRepository;
import com.karyaanakbangsa.perpustakaan.security.SecurityUtil;
import com.karyaanakbangsa.perpustakaan.repository.BorrowRepository;
import com.karyaanakbangsa.perpustakaan.repository.BukuRepository;
import com.karyaanakbangsa.perpustakaan.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final BukuRepository bukuRepository;
    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, BukuRepository bukuRepository, UserRepository userRepository,
            BorrowRepository borrowRepository) {
        this.cartRepository = cartRepository;
        this.bukuRepository = bukuRepository;
        this.userRepository = userRepository;
        this.borrowRepository = borrowRepository;
    }

    @Override
    @Transactional
    public CartDto getCart() {
        String username = SecurityUtil.getSessionUser();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }

        UserEntity currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }

        Cart cart = cartRepository.findByCreatedBy(currentUser)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .createdBy(currentUser)
                            .bukus(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });

        List<BukuDto> bukuDtos = cart.getBukus().stream()
                .map(buku -> BukuDto.builder()
                        .id(buku.getId())
                        .judul(buku.getJudul())
                        .pengarang(buku.getPengarang())
                        .penerbit(buku.getPenerbit())
                        .tahunTerbit(buku.getTahunTerbit())
                        .maxPinjam(buku.getMaxPinjam())
                        .available(buku.getAvailable())
                        .build())
                .collect(Collectors.toList());

        return CartDto.builder()
                .id(cart.getId())
                .bukuDtos(bukuDtos)
                .checkout_date(cart.getCheckout_date())
                .build();
    }

    @Override
    @Transactional
    public void addToCart(int bukuId) {
        String username = SecurityUtil.getSessionUser();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }

        UserEntity currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("User not found for username: " + username);
        }

        Cart cart = cartRepository.findByCreatedBy(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Buku buku = bukuRepository.findById(bukuId)
                .orElseThrow(() -> new RuntimeException("Buku not found"));

        if (!buku.getAvailable()) {
            throw new RuntimeException("Buku tidak tersedia untuk ditambahkan ke cart.");
        }

        if (cart.getBukus().contains(buku)) {
            throw new RuntimeException("Buku sudah ada dalam cart.");
        }

        cart.getBukus().add(buku);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(int bukuId) {
        String username = SecurityUtil.getSessionUser();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }

        UserEntity currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }

        Cart cart = cartRepository.findByCreatedBy(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Buku buku = bukuRepository.findById(bukuId)
                .orElseThrow(() -> new RuntimeException("Buku not found"));

        cart.getBukus().remove(buku);
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCartDto> getAllCartsForAdmin() {
        return userRepository.findAll().stream()
                .filter(user -> cartRepository.existsByCreatedBy(user)) // Pastikan user memiliki cart
                .map(user -> {
                    List<BukuDto> bukuDtos = getCartItemsByUserId(user.getId());
                    // Hanya masukkan pengguna jika cart mereka memiliki buku
                    if (bukuDtos.isEmpty()) {
                        return null; // Kembalikan null untuk cart kosong
                    }
                    return UserCartDto.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .bukuDtos(bukuDtos)
                            .build();
                })
                .filter(userCartDto -> userCartDto != null) // Hilangkan entri null
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BukuDto> getCartItemsByUserId(int userId) {
        return cartRepository.findCartDetailsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getUsersWithCarts() {
        // Fetch all users who have an associated cart
        return userRepository.findAll().stream()
                .filter(user -> cartRepository.existsByCreatedBy(user)) // Check if the user has a cart
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkoutBooks(int userId, List<Integer> bukuIds) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByCreatedBy(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<Buku> bukusToCheckout = cart.getBukus().stream()
                .filter(buku -> bukuIds.contains(buku.getId()))
                .collect(Collectors.toList());

        if (bukusToCheckout.isEmpty()) {
            throw new RuntimeException("Cart is empty or no books to checkout.");
        }

        for (Buku buku : bukusToCheckout) {
            if (!buku.getAvailable()) {
                throw new RuntimeException("Buku " + buku.getJudul() + " tidak tersedia.");
            }

            // Tandai buku sebagai tidak tersedia
            buku.setAvailable(false);
            bukuRepository.save(buku);

            // Simpan data peminjaman
            Borrow borrow = Borrow.builder()
                    .buku(buku)
                    .user(user)
                    .tanggalPinjam(LocalDate.now())
                    .tenggatKembali(LocalDate.now().plusDays(buku.getMaxPinjam()))
                    .returned(false)
                    .denda(0)
                    .build();
            borrowRepository.save(borrow);
        }

        // Hapus buku yang diproses dari cart
        cart.getBukus().removeAll(bukusToCheckout);
        cartRepository.save(cart);
    }

}
