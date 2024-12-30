package com.karyaanakbangsa.perpustakaan.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CartDto {
    private int id;
    private LocalDate checkout_date;
    private List<BukuDto> bukuDtos;
    // List buku yang ada di dalam cart
}
