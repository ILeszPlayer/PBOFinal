package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateCommentRequest;
import com.example.smartcommunity.model.Comment;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.CommentRepository;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              ComplaintRepository complaintRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
    }

    @Override
    public Comment addComment(CreateCommentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        Complaint complaint = complaintRepository.findById(request.getComplaintId())
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));

        Comment comment = new Comment(request.getIsiKomentar(), user, complaint);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByComplaint(Long complaintId) {
        return commentRepository.findByComplaintIdOrderByTanggalAsc(complaintId);
    }
}
