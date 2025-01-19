package com.karyaanakbangsa.perpustakaan.repository;

import java.lang.classfile.ClassFile.Option;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karyaanakbangsa.perpustakaan.models.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByNoTelepon(String noTelepon);

}
