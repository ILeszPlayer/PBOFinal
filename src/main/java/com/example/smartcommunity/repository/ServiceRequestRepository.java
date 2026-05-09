package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {}
