package com.karyaanakbangsa.perpustakaan.dto;

import java.util.List;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserCartDto {
    private int userId;
    private String username;
    private List<BukuDto> bukuDtos;

    // Public constructor for the builder pattern
    public UserCartDto(int userId, String username, List<BukuDto> bukuDtos) {
        this.userId = userId;
        this.username = username;
        this.bukuDtos = bukuDtos;
    }
}


