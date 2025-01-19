package com.karyaanakbangsa.perpustakaan.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "member")
public class Member extends UserEntity {
    @Column(unique = true, nullable = false)
    private String noTelepon;
}
