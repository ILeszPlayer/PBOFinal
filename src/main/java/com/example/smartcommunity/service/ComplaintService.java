package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.Complaint;

import java.util.List;

public interface ComplaintService {
    Complaint createComplaint(CreateComplaintRequest request);
    Complaint updateStatus(Long complaintId, Complaint.Status status);
    List<Complaint> getAllComplaints();
    List<Complaint> getComplaintsByUser(Long userId);
    List<Complaint> getComplaintsByCategory(Complaint.Kategori kategori);
    List<Complaint> getComplaintsByStatus(Complaint.Status status);
    Complaint findById(Long id);
    long countByStatus(Complaint.Status status);
    long countAll();
}
