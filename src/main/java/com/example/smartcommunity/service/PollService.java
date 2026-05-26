package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreatePollRequest;
import com.example.smartcommunity.dto.PollVoteRequest;
import com.example.smartcommunity.model.Poll;

import java.util.List;

public interface PollService {
    Poll createPoll(CreatePollRequest request);
    Poll castVote(PollVoteRequest request);
    Poll closePoll(Long pollId);
    List<Poll> getActivePolls();
    List<Poll> getAllPolls();
}
