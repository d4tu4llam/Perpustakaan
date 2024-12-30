package com.karyaanakbangsa.perpustakaan.dto;

import java.time.LocalDate;

import com.karyaanakbangsa.perpustakaan.models.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowDto {
    private int borrowId;
    private int bookId;
    private String judul;
    private String pengarang;
    private String penerbit;
    private Integer tahunTerbit;
    private LocalDate tanggalPinjam;
    private LocalDate tenggatKembali;
    private LocalDate tanggalPengembalian;
    private boolean returned;
    private long denda;
    private UserEntity user;
}
