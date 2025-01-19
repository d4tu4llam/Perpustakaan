package com.karyaanakbangsa.perpustakaan.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "admin")
public class Admin extends UserEntity {
    @Column(unique = true, nullable = false)
    private String nip;
}
