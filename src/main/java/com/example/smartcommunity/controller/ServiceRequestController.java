package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateDocumentRequest;
import com.example.smartcommunity.dto.CreateServiceRequestDto;
import com.example.smartcommunity.dto.UpdateStatusRequest;
import com.example.smartcommunity.model.ServiceDocument;
import com.example.smartcommunity.model.ServiceHistory;
import com.example.smartcommunity.model.ServiceRequest;
import com.example.smartcommunity.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/service-requests")
public class ServiceRequestController {
    private final ServiceRequestService serviceRequestService;
    public ServiceRequestController(ServiceRequestService serviceRequestService) { this.serviceRequestService = serviceRequestService; }

    @GetMapping
    public List<ServiceRequest> getAllRequests() { return serviceRequestService.findAll(); }

    @GetMapping("/{id}")
    public ServiceRequest getRequestById(@PathVariable Long id) { return serviceRequestService.findById(id); }

    @PostMapping
    public ServiceRequest createRequest(@Valid @RequestBody CreateServiceRequestDto request) { return serviceRequestService.create(request); }

    @PutMapping("/{id}/status")
    public ServiceRequest updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) { return serviceRequestService.updateStatus(id, request); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) { serviceRequestService.delete(id); return ResponseEntity.noContent().build(); }

    @PostMapping("/{id}/documents")
    public ServiceDocument addDocument(@PathVariable Long id, @Valid @RequestBody CreateDocumentRequest request) { return serviceRequestService.addDocument(id, request); }

    @GetMapping("/{id}/history")
    public List<ServiceHistory> getHistory(@PathVariable Long id) { return serviceRequestService.getHistory(id); }
}
