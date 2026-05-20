package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreateCommentRequest;
import com.example.smartcommunity.model.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(CreateCommentRequest request);
    List<Comment> getCommentsByComplaint(Long complaintId);
}
