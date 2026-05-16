package com.example.smartcommunity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smartcommunity.model.ServiceRequest;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
}