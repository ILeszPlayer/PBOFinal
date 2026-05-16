package com.example.smartcommunity.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.service.ComplaintService;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository){
        this.complaintRepository = complaintRepository;
    }

    @Override
    public List<Complaint> getAllComplaints(){
        return complaintRepository.findAll();
    }

    @Override
    public Complaint saveComplaint(Complaint complaint){
        return complaintRepository.save(complaint);
    }
}