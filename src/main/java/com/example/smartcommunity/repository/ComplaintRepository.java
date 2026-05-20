package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByKategori(Complaint.Kategori kategori);
    List<Complaint> findByStatus(Complaint.Status status);
    List<Complaint> findByUserIdOrderByTanggalDesc(Long userId);
    List<Complaint> findAllByOrderByTanggalDesc();
    long countByStatus(Complaint.Status status);
}
