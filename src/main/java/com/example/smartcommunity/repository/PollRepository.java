package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByIsActiveTrueOrderByCreatedAtDesc();
    List<Poll> findAllByOrderByCreatedAtDesc();
}
