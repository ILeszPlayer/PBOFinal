package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByComplaintIdOrderByTanggalAsc(Long complaintId);

    @Query(value = "SELECT c.id, c.isi_komentar, c.tanggal, u.nama FROM comments c JOIN users u ON c.user_id = u.id WHERE c.complaint_id = :complaintId ORDER BY c.tanggal ASC", nativeQuery = true)
    List<Object[]> findCommentRawDataByComplaintId(@Param("complaintId") Long complaintId);
}
