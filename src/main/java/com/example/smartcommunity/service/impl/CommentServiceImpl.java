package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateCommentRequest;
import com.example.smartcommunity.model.Comment;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Notification;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.repository.CommentRepository;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.CommentService;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.PenggunaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PenggunaRepository penggunaRepository;
    private final ComplaintRepository complaintRepository;
    private final PenggunaService penggunaService;
    private final NotificationService notificationService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PenggunaRepository penggunaRepository,
                              ComplaintRepository complaintRepository,
                              PenggunaService penggunaService,
                              NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.penggunaRepository = penggunaRepository;
        this.complaintRepository = complaintRepository;
        this.penggunaService = penggunaService;
        this.notificationService = notificationService;
    }

    @Override
    public Comment addComment(CreateCommentRequest request) {
        Pengguna user = penggunaRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        Complaint complaint = complaintRepository.findById(request.getComplaintId())
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));

        Comment comment = new Comment(request.getIsiKomentar(), user, complaint);
        comment = commentRepository.save(comment);
        penggunaService.awardPoints(request.getUserId(), 5);

        try {
            Pengguna complaintOwner = complaint.getUser();
            if (!complaintOwner.getId().equals(user.getId())) {
                notificationService.createNotification(
                    Notification.Type.NEW_COMMENT,
                    user.getNama() + " berkomentar pada pengaduan \"" + complaint.getJudul() + "\".",
                    complaintOwner,
                    complaint
                );
            }
        } catch (Exception ignored) {}

        return comment;
    }

    @Override
    public List<Comment> getCommentsByComplaint(Long complaintId) {
        return commentRepository.findByComplaintIdOrderByTanggalAsc(complaintId);
    }
}
