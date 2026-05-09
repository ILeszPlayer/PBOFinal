package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.ServiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory, Long> {
    List<ServiceHistory> findByServiceRequestIdRequest(Long idRequest);
}
