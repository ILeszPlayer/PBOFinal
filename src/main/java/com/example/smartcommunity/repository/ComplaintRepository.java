package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByKategori(Complaint.Kategori kategori);
    List<Complaint> findByStatus(Complaint.Status status);
    List<Complaint> findByUrgency(Complaint.Urgency urgency);
    List<Complaint> findByUserIdOrderByTanggalDesc(Long userId);
    List<Complaint> findAllByOrderByTanggalDesc();
    List<Complaint> findAllByOrderByUpvotesCountDesc();
    List<Complaint> findByJudulContainingIgnoreCaseOrIsiPengaduanContainingIgnoreCase(String judul, String isi);
    List<Complaint> findByLatitudeIsNotNullAndLongitudeIsNotNull();
    long countByStatus(Complaint.Status status);

    @Query("SELECT c.kategori, COUNT(c) FROM Complaint c GROUP BY c.kategori")
    List<Object[]> countGroupByKategori();

    @Query("SELECT c.urgency, COUNT(c) FROM Complaint c GROUP BY c.urgency")
    List<Object[]> countGroupByUrgency();

    @Query("SELECT FUNCTION('MONTH', c.tanggal), COUNT(c) FROM Complaint c WHERE c.status = 'SELESAI' GROUP BY FUNCTION('MONTH', c.tanggal) ORDER BY FUNCTION('MONTH', c.tanggal)")
    List<Object[]> countResolvedByMonth();
}
