package com.karyaanakbangsa.perpustakaan.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buku_id", nullable = false)
    private Buku buku;

    private LocalDate tanggalPinjam;

    private LocalDate tenggatKembali;
    private LocalDate tanggalPengembalian;
    private Boolean returned;
    private long denda;
}
