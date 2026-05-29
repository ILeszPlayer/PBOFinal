package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Complaint> findAllByOrderByUpvotesCountDesc(Pageable pageable);
    List<Complaint> findAllByOrderByUpvotesCountDesc();
    List<Complaint> findByJudulContainingIgnoreCaseOrIsiPengaduanContainingIgnoreCase(String judul, String isi);
    List<Complaint> findByLatitudeIsNotNullAndLongitudeIsNotNull();
    long countByStatus(Complaint.Status status);
    long countByStatus(String status);

    @Query(value = "SELECT kategori, COUNT(id) FROM complaints GROUP BY kategori", nativeQuery = true)
    List<Object[]> countGroupByKategori();

    @Query(value = "SELECT urgency, COUNT(id) FROM complaints GROUP BY urgency", nativeQuery = true)
    List<Object[]> countGroupByUrgency();

    @Query(value = "SELECT MONTH(tanggal), COUNT(id) FROM complaints WHERE status = 'SELESAI' GROUP BY MONTH(tanggal) ORDER BY MONTH(tanggal)", nativeQuery = true)
    List<Object[]> countResolvedByMonth();

    @Query(value = "SELECT MONTH(tanggal), COUNT(id) FROM complaints WHERE status = ?1 GROUP BY MONTH(tanggal) ORDER BY MONTH(tanggal)", nativeQuery = true)
    List<Object[]> getMonthlyResolutionStats(String status);

    @Query(value = "SELECT MONTH(tanggal), COUNT(id) FROM complaints GROUP BY MONTH(tanggal) ORDER BY MONTH(tanggal)", nativeQuery = true)
    List<Object[]> getMonthlyStats();

    @Query(value = "SELECT c.id, c.judul, c.isi_pengaduan, c.kategori, c.status, c.urgency, c.bukti_foto, c.upvotes_count, c.is_anonymous, c.tanggal, u.nama FROM complaints c LEFT JOIN users u ON c.user_id = u.id WHERE c.id = :id", nativeQuery = true)
    List<Object[]> findComplaintRawDataById(@org.springframework.data.repository.query.Param("id") Long id);
}
