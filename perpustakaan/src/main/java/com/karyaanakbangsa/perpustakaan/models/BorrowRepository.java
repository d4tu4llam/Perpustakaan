package com.karyaanakbangsa.perpustakaan.repository;

import com.karyaanakbangsa.perpustakaan.models.Borrow;
import com.karyaanakbangsa.perpustakaan.models.UserEntity;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {
    List<Borrow> findByUser(UserEntity user);

    List<Borrow> findByIdIn(List<Integer> borrowIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM Borrow b WHERE b.buku.id = :bukuId")
    void deleteByBukuId(@Param("bukuId") int bukuId);
}
