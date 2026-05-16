package com.example.smartcommunity.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.smartcommunity.model.ServiceRequest;
import com.example.smartcommunity.repository.ServiceRequestRepository;
import com.example.smartcommunity.service.ServiceRequestService;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository repository;

    public ServiceRequestServiceImpl(ServiceRequestRepository repository){
        this.repository = repository;
    }

    @Override
    public List<ServiceRequest> getAllServices(){
        return repository.findAll();
    }

    @Override
    public ServiceRequest saveService(ServiceRequest serviceRequest){
        return repository.save(serviceRequest);
    }
}