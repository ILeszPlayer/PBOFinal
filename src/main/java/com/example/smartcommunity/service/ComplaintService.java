package com.example.smartcommunity.service;

import java.util.List;

import com.example.smartcommunity.model.Complaint;

public interface ComplaintService {

    List<Complaint> getAllComplaints();

    Complaint saveComplaint(Complaint complaint);
}