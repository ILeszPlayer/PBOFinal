package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.Broadcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, Long> {
    List<Broadcast> findAllByOrderByTanggalDesc();
    List<Broadcast> findAllByOrderByIdDesc();
}
