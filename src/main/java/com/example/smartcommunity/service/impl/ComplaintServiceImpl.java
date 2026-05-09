package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.dto.UpdateStatusRequest;
import com.example.smartcommunity.exception.ResourceNotFoundException;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.ComplaintService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    public List<Complaint> findAll() { return complaintRepository.findAll(); }

    public Complaint findById(Long id) {
        return complaintRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pengaduan tidak ditemukan"));
    }

    public Complaint create(CreateComplaintRequest request) {
        User user = userRepository.findById(request.userId).orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setJudul(request.judul);
        complaint.setIsiPengaduan(request.isiPengaduan);
        return complaintRepository.save(complaint);
    }

    public Complaint update(Long id, CreateComplaintRequest request) {
        Complaint complaint = findById(id);
        complaint.setJudul(request.judul);
        complaint.setIsiPengaduan(request.isiPengaduan);
        return complaintRepository.save(complaint);
    }

    public Complaint updateStatus(Long id, UpdateStatusRequest request) {
        Complaint complaint = findById(id);
        complaint.setStatus(request.status);
        return complaintRepository.save(complaint);
    }

    public void delete(Long id) {
        complaintRepository.delete(findById(id));
    }
}
