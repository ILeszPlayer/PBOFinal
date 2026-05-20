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
    List<Complaint> searchComplaints(String keyword);
    List<Complaint> getComplaintsByUser(Long userId);
    List<Complaint> getComplaintsByCategory(Complaint.Kategori kategori);
    List<Complaint> getComplaintsByStatus(Complaint.Status status);
    List<Complaint> getComplaintsByUrgency(Complaint.Urgency urgency);
    List<Complaint> getComplaintsSortedByUpvotes();
    List<Complaint> getGeoTaggedComplaints();
    Complaint findById(Long id);
    long countByStatus(Complaint.Status status);
    long countAll();
    List<Object[]> countByKategori();
    List<Object[]> countByUrgency();
    List<Object[]> countResolvedByMonth();
    double getAverageSlaHours();
}
