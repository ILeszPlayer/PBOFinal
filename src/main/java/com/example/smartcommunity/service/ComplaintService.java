package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.Complaint;

import java.util.List;

public interface ComplaintService {
    Complaint createComplaint(CreateComplaintRequest request);
    Complaint updateStatus(Long complaintId, Complaint.Status status);
    Complaint upvote(Long complaintId);
    void deleteComplaint(Long complaintId);
    List<Complaint> getAllComplaints();
    List<Complaint> getComplaintsByUser(Long userId);
    List<Complaint> getComplaintsByCategory(Complaint.Kategori kategori);
    List<Complaint> getComplaintsByStatus(Complaint.Status status);
    List<Complaint> getComplaintsSortedByUpvotes();
    Complaint findById(Long id);
    long countByStatus(Complaint.Status status);
    long countAll();
}
