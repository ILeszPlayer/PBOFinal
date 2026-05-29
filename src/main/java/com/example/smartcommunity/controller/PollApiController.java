package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.model.Poll;
import com.example.smartcommunity.model.PollVote;
import com.example.smartcommunity.repository.PollRepository;
import com.example.smartcommunity.repository.PollVoteRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/polls")
public class PollApiController {

    private final PollRepository pollRepository;
    private final PollVoteRepository pollVoteRepository;
    private final PenggunaRepository penggunaRepository;

    public PollApiController(PollRepository pollRepository,
                             PollVoteRepository pollVoteRepository,
                             PenggunaRepository penggunaRepository) {
        this.pollRepository = pollRepository;
        this.pollVoteRepository = pollVoteRepository;
        this.penggunaRepository = penggunaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getActivePolls(Authentication authentication) {
        List<Poll> polls = pollRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        Long userId = getUserId(authentication);

        List<Map<String, Object>> result = polls.stream().map(poll -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", poll.getId());
            m.put("question", poll.getQuestion());
            m.put("isActive", poll.isIsActive());
            m.put("createdAt", poll.getCreatedAt() != null ?
                List.of(poll.getCreatedAt().getYear(), poll.getCreatedAt().getMonthValue(),
                    poll.getCreatedAt().getDayOfMonth()) : null);
            m.put("yesCount", poll.getYesCount());
            m.put("noCount", poll.getNoCount());
            m.put("totalVotes", poll.getTotalVotes());
            m.put("yesPercentage", Math.round(poll.getYesPercentage() * 10.0) / 10.0);
            m.put("noPercentage", Math.round(poll.getNoPercentage() * 10.0) / 10.0);
            m.put("hasVoted", userId != null && poll.hasUserVoted(userId));
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @Transactional
    @PostMapping("/{id}/vote")
    public ResponseEntity<Map<String, Object>> castVote(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Harus login"));
        }
        String voteStr = body.get("vote");
        if (voteStr == null || (!voteStr.equals("YES") && !voteStr.equals("NO"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Pilih YES atau NO"));
        }

        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Polling tidak ditemukan"));
        if (!poll.isIsActive()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Polling sudah ditutup"));
        }

        Pengguna user = penggunaRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (pollVoteRepository.findByPollIdAndUserId(id, user.getId()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Anda sudah memberikan suara"));
        }

        PollVote.Vote vote = PollVote.Vote.valueOf(voteStr);
        PollVote pollVote = new PollVote(vote, poll, user);
        pollVoteRepository.save(pollVote);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "yesCount", poll.getYesCount(),
            "noCount", poll.getNoCount(),
            "totalVotes", poll.getTotalVotes()
        ));
    }

    private Long getUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        try {
            Pengguna user = penggunaRepository.findByEmail(authentication.getName()).orElse(null);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
