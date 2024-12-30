package com.karyaanakbangsa.perpustakaan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.karyaanakbangsa.perpustakaan.dto.BukuDto;
import com.karyaanakbangsa.perpustakaan.models.Cart;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findAll();

    Optional<Cart> findByCreatedBy(UserEntity createdBy); // cari cart by user

    @Query("SELECT new com.karyaanakbangsa.perpustakaan.dto.BukuDto(b.id, b.judul, b.pengarang, b.penerbit, b.tahunTerbit, b.maxPinjam, b.available, b.category) "
            +
            "FROM Cart c JOIN c.bukus b WHERE c.createdBy.id = :userId")
    List<BukuDto> findCartDetailsByUserId(@Param("userId") int userId);

    boolean existsByCreatedBy(UserEntity created);

    @Query("SELECT DISTINCT c FROM Cart c JOIN FETCH c.createdBy")
    List<Cart> findAllWithUsers();

}
