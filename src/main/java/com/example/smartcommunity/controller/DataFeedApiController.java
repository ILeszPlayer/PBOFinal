package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DataFeedApiController {

    @Autowired private ComplaintRepository complaintRepository;

    @GetMapping("/feed/complaints")
    public ResponseEntity<List<Complaint>> getComplaintsAsync() {
        List<Complaint> complaints = complaintRepository.findAllByOrderByUpvotesCountDesc();
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/complaints/{id}")
    public ResponseEntity<Complaint> getComplaint(@PathVariable Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));
        return ResponseEntity.ok(complaint);
    }

    @PostMapping("/complaints/{id}/upvote")
    public ResponseEntity<Integer> upvoteComplaint(@PathVariable Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan tidak ditemukan"));
        complaint.setUpvotesCount(complaint.getUpvotesCount() + 1);
        complaintRepository.save(complaint);
        return ResponseEntity.ok(complaint.getUpvotesCount());
    }
}
