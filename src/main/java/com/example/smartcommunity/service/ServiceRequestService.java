package com.example.smartcommunity.service;

import java.util.List;

import com.example.smartcommunity.model.ServiceRequest;

public interface ServiceRequestService {

    List<ServiceRequest> getAllServices();

    ServiceRequest saveService(ServiceRequest serviceRequest);
}