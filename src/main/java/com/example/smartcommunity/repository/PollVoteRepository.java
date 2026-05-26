package com.example.smartcommunity.repository;

import com.example.smartcommunity.model.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, Long> {
    Optional<PollVote> findByPollIdAndUserId(Long pollId, Long userId);
    long countByPollIdAndVote(Long pollId, PollVote.Vote vote);
}
