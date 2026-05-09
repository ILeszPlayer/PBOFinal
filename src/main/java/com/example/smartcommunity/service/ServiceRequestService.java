package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreateDocumentRequest;
import com.example.smartcommunity.dto.CreateServiceRequestDto;
import com.example.smartcommunity.dto.UpdateStatusRequest;
import com.example.smartcommunity.model.ServiceDocument;
import com.example.smartcommunity.model.ServiceHistory;
import com.example.smartcommunity.model.ServiceRequest;
import java.util.List;

public interface ServiceRequestService {
    List<ServiceRequest> findAll();
    ServiceRequest findById(Long id);
    ServiceRequest create(CreateServiceRequestDto request);
    ServiceRequest updateStatus(Long id, UpdateStatusRequest request);
    void delete(Long id);
    ServiceDocument addDocument(Long id, CreateDocumentRequest request);
    List<ServiceHistory> getHistory(Long id);
}
