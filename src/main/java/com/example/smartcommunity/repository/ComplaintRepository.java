package com.example.smartcommunity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smartcommunity.model.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}