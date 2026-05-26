package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreatePollRequest;
import com.example.smartcommunity.dto.PollVoteRequest;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.model.Poll;
import com.example.smartcommunity.model.PollVote;
import com.example.smartcommunity.repository.PollRepository;
import com.example.smartcommunity.repository.PollVoteRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.PollService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final PollVoteRepository pollVoteRepository;
    private final PenggunaRepository penggunaRepository;

    public PollServiceImpl(PollRepository pollRepository,
                           PollVoteRepository pollVoteRepository,
                           PenggunaRepository penggunaRepository) {
        this.pollRepository = pollRepository;
        this.pollVoteRepository = pollVoteRepository;
        this.penggunaRepository = penggunaRepository;
    }

    @Override
    public Poll createPoll(CreatePollRequest request) {
        Pengguna admin = penggunaRepository.findById(request.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        Poll poll = new Poll(request.getQuestion(), admin);
        return pollRepository.save(poll);
    }

    @Override
    public Poll castVote(PollVoteRequest request) {
        Poll poll = pollRepository.findById(request.getPollId())
                .orElseThrow(() -> new RuntimeException("Polling tidak ditemukan"));

        if (!poll.isIsActive()) {
            throw new RuntimeException("Polling sudah ditutup");
        }

        if (poll.hasUserVoted(request.getUserId())) {
            throw new RuntimeException("Anda sudah memberikan suara");
        }

        Pengguna user = penggunaRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        PollVote.Vote vote = PollVote.Vote.valueOf(request.getVote().toUpperCase());
        PollVote pollVote = new PollVote(vote, poll, user);
        pollVoteRepository.save(pollVote);

        return pollRepository.findById(request.getPollId()).orElse(poll);
    }

    @Override
    public Poll closePoll(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Polling tidak ditemukan"));
        poll.setIsActive(false);
        poll.setClosedAt(LocalDateTime.now());
        return pollRepository.save(poll);
    }

    @Override
    public List<Poll> getActivePolls() {
        return pollRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    public List<Poll> getAllPolls() {
        return pollRepository.findAllByOrderByCreatedAtDesc();
    }
}
