package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateCommentRequest;
import com.example.smartcommunity.model.Comment;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Notification;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.CommentRepository;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.CommentService;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              ComplaintRepository complaintRepository,
                              UserService userService,
                              NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public Comment addComment(CreateCommentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        Complaint complaint = complaintRepository.findById(request.getComplaintId())
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));

        Comment comment = new Comment(request.getIsiKomentar(), user, complaint);
        comment = commentRepository.save(comment);
        userService.awardPoints(request.getUserId(), 5);

        try {
            User complaintOwner = complaint.getUser();
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
