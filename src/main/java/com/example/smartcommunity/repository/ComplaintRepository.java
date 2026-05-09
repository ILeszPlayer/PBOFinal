package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {}
