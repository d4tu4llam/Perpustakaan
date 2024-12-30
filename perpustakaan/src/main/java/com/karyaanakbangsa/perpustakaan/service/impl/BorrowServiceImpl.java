package com.karyaanakbangsa.perpustakaan.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.karyaanakbangsa.perpustakaan.dto.BorrowDto;
import com.karyaanakbangsa.perpustakaan.models.Borrow;
import com.karyaanakbangsa.perpustakaan.models.Buku;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;
import com.karyaanakbangsa.perpustakaan.repository.BorrowRepository;
import com.karyaanakbangsa.perpustakaan.repository.BukuRepository;
import com.karyaanakbangsa.perpustakaan.repository.UserRepository;
import com.karyaanakbangsa.perpustakaan.service.BorrowService;

@Service
public class BorrowServiceImpl implements BorrowService {
    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;
    private final BukuRepository bukuRepository;

    @Autowired
    public BorrowServiceImpl(UserRepository userRepository, BorrowRepository borrowRepository,
            BukuRepository bukuRepository) {
        this.userRepository = userRepository;
        this.borrowRepository = borrowRepository;
        this.bukuRepository = bukuRepository;
    }

    private Borrow mapToBorrow(BorrowDto borrowDto) {
        System.out.println("bookId passed to mapToBorrow: " + borrowDto.getBookId());
        Buku buku = bukuRepository.findById(borrowDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        System.out.println("tanggalnotdto" + borrowDto.getTenggatKembali());
        return Borrow.builder()
                .user(borrowDto.getUser())
                .id(borrowDto.getBorrowId())
                .buku(buku) // Map the Buku entity
                .tanggalPinjam(borrowDto.getTanggalPinjam())
                .tenggatKembali(borrowDto.getTenggatKembali())
                .tanggalPengembalian(borrowDto.getTanggalPengembalian())
                .returned(borrowDto.isReturned())
                .denda(borrowDto.getDenda())
                .build();
    }

    private BorrowDto mapToBorrowDto(Borrow borrow) {
        System.out.println("tanggaldto" + borrow.getTenggatKembali());
        BorrowDto borrowDto = BorrowDto.builder()
                .user(borrow.getUser())
                .borrowId(borrow.getId()) // borrowId ada di entitas Borrow
                .bookId(borrow.getBuku().getId()) // bookId diambil dari Buku yang dipinjam
                .judul(borrow.getBuku().getJudul())
                .pengarang(borrow.getBuku().getPengarang())
                .penerbit(borrow.getBuku().getPenerbit())
                .tahunTerbit(borrow.getBuku().getTahunTerbit())
                .tanggalPinjam(borrow.getTanggalPinjam())
                .tenggatKembali(borrow.getTenggatKembali())
                .returned(borrow.getReturned())
                .denda(borrow.getDenda())
                .build();
        return borrowDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowDto> getBorrowedBooks(int userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Borrow> borrows = borrowRepository.findByUser(user);

        // Map borrows ke BorrowDto
        return borrows.stream()
                .map(borrow -> BorrowDto.builder()
                        .borrowId(borrow.getId()) // borrowId ada di entitas Borrow
                        .bookId(borrow.getBuku().getId()) // bookId diambil dari Buku yang dipinjam
                        .judul(borrow.getBuku().getJudul())
                        .pengarang(borrow.getBuku().getPengarang())
                        .penerbit(borrow.getBuku().getPenerbit())
                        .tahunTerbit(borrow.getBuku().getTahunTerbit())
                        .tanggalPinjam(borrow.getTanggalPinjam())
                        .tenggatKembali(borrow.getTenggatKembali())
                        .tanggalPengembalian(borrow.getTanggalPengembalian())
                        .denda(borrow.getDenda())
                        .returned(borrow.getReturned())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void returnAndRemoveBooks(List<Integer> borrowIds) {
        List<Borrow> borrows = borrowRepository.findByIdIn(borrowIds);

        for (Borrow borrow : borrows) {
            Buku buku = borrow.getBuku();

            // Mengubah status buku menjadi tersedia
            buku.setAvailable(true);
            bukuRepository.save(buku);
            LocalDate tenggatKembali = borrow.getTenggatKembali();
            LocalDate tanggalPengembalian = LocalDate.now();
            long daysLate = ChronoUnit.DAYS.between(tenggatKembali, tanggalPengembalian);
            if (daysLate > 0) {
                // Jika keterlambatan lebih dari 0 hari, hitung denda
                int fine = (int) (daysLate * 5000); // Casting to int for denda field
                if (fine > 50000) {
                    fine = 50000;
                }
                borrow.setDenda(fine);
            }
            // Menghapus entri borrow (peminjaman)

            borrow.setTanggalPengembalian(tanggalPengembalian);
            borrow.setReturned(true);

            borrowRepository.save(borrow);
        }

    }

    @Override
    public void editBorrow(BorrowDto borrowDto) {
        Borrow borrow = mapToBorrow(borrowDto);
        borrowRepository.save(borrow);
    }

    @Override
    public BorrowDto findBorrowById(int borrowIds) {
        Borrow borrow = borrowRepository.findById(borrowIds).get();

        return mapToBorrowDto(borrow);

    }

}
