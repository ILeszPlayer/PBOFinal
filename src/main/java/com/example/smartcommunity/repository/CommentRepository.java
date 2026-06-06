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

    @Query(value = "SELECT c.id, c.isi_komentar, c.tanggal, u.nama, u.id, COALESCE(c.like_count, 0) FROM comments c JOIN users u ON c.user_id = u.id WHERE c.complaint_id = :complaintId ORDER BY c.tanggal ASC", nativeQuery = true)
    List<Object[]> findCommentRawDataByComplaintId(@Param("complaintId") Long complaintId);

    @Query(value = "SELECT cl.comment_id, cl.user_id FROM comment_likes cl JOIN comments c ON c.id = cl.comment_id WHERE c.complaint_id = :complaintId", nativeQuery = true)
    List<Object[]> findCommentLikesByComplaintId(@Param("complaintId") Long complaintId);
}
