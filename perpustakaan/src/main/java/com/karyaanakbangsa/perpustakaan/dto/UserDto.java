package com.karyaanakbangsa.perpustakaan.dto;

import groovy.transform.builder.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private int userId;
    private String username;

    public UserDto(int userId, String username) {
        this.userId = userId;
        this.username = username;

    }

}
