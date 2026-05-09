package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.dto.UpdateStatusRequest;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/complaints")
public class ComplaintController {
    private final ComplaintService complaintService;
    public ComplaintController(ComplaintService complaintService) { this.complaintService = complaintService; }

    @GetMapping
    public List<Complaint> getAllComplaints() { return complaintService.findAll(); }

    @GetMapping("/{id}")
    public Complaint getComplaintById(@PathVariable Long id) { return complaintService.findById(id); }

    @PostMapping
    public Complaint createComplaint(@Valid @RequestBody CreateComplaintRequest request) { return complaintService.create(request); }

    @PutMapping("/{id}")
    public Complaint updateComplaint(@PathVariable Long id, @Valid @RequestBody CreateComplaintRequest request) { return complaintService.update(id, request); }

    @PutMapping("/{id}/status")
    public Complaint updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) { return complaintService.updateStatus(id, request); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) { complaintService.delete(id); return ResponseEntity.noContent().build(); }
}
