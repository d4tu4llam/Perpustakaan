package com.karyaanakbangsa.perpustakaan.service;

import java.util.List;

import com.karyaanakbangsa.perpustakaan.dto.BorrowDto;

public interface BorrowService {
    List<BorrowDto> getBorrowedBooks(int userId);

    void returnAndRemoveBooks(List<Integer> borrowIds);

    BorrowDto findBorrowById(int borrowIds);

    void editBorrow(BorrowDto borrowDto);

}
