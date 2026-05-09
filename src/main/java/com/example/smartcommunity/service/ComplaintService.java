package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.dto.UpdateStatusRequest;
import com.example.smartcommunity.model.Complaint;
import java.util.List;

public interface ComplaintService {
    List<Complaint> findAll();
    Complaint findById(Long id);
    Complaint create(CreateComplaintRequest request);
    Complaint update(Long id, CreateComplaintRequest request);
    Complaint updateStatus(Long id, UpdateStatusRequest request);
    void delete(Long id);
}
