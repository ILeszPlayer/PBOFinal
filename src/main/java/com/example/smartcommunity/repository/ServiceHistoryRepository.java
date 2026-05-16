package com.example.smartcommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartcommunity.model.ServiceHistory;

@Repository
public interface ServiceHistoryRepository
        extends JpaRepository<ServiceHistory, Long> {

    List<ServiceHistory> findByServiceRequestId(Long id);

}