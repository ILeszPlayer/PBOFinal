package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, Long> {
    boolean existsByEmail(String email);
    Optional<Pengguna> findByEmail(String email);
    Optional<Pengguna> findByNama(String nama);
    List<Pengguna> findByRole(String role);
}
