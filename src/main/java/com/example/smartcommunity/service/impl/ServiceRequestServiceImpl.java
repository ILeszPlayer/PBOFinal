package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateDocumentRequest;
import com.example.smartcommunity.dto.CreateServiceRequestDto;
import com.example.smartcommunity.dto.UpdateStatusRequest;
import com.example.smartcommunity.exception.ResourceNotFoundException;
import com.example.smartcommunity.model.*;
import com.example.smartcommunity.repository.*;
import com.example.smartcommunity.service.ServiceRequestService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final AdministrativeServiceRepository serviceRepository;
    private final ServiceHistoryRepository historyRepository;
    private final ServiceDocumentRepository documentRepository;

    public ServiceRequestServiceImpl(ServiceRequestRepository serviceRequestRepository, UserRepository userRepository,
                                     AdministrativeServiceRepository serviceRepository, ServiceHistoryRepository historyRepository,
                                     ServiceDocumentRepository documentRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.historyRepository = historyRepository;
        this.documentRepository = documentRepository;
    }

    public List<ServiceRequest> findAll() { return serviceRequestRepository.findAll(); }

    public ServiceRequest findById(Long id) {
        return serviceRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pengajuan layanan tidak ditemukan"));
    }

    public ServiceRequest create(CreateServiceRequestDto request) {
        User user = userRepository.findById(request.userId).orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
        AdministrativeService service = serviceRepository.findById(request.serviceId).orElseThrow(() -> new ResourceNotFoundException("Layanan tidak ditemukan"));
        GeneralServiceRequest serviceRequest = new GeneralServiceRequest();
        serviceRequest.setUser(user);
        serviceRequest.setAdministrativeService(service);
        serviceRequest.setNomorRequest("REQ-" + System.currentTimeMillis());
        serviceRequest.setCatatanAdmin(request.catatanAdmin);
        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);
        addHistory(saved, "Pengajuan layanan dibuat");
        return saved;
    }

    public ServiceRequest updateStatus(Long id, UpdateStatusRequest request) {
        ServiceRequest serviceRequest = findById(id);
        serviceRequest.setStatus(request.status);
        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);
        addHistory(saved, request.keterangan != null ? request.keterangan : "Status diubah menjadi " + request.status);
        return saved;
    }

    public void delete(Long id) { serviceRequestRepository.delete(findById(id)); }

    public ServiceDocument addDocument(Long id, CreateDocumentRequest request) {
        ServiceRequest serviceRequest = findById(id);
        ServiceDocument document = new ServiceDocument();
        document.setServiceRequest(serviceRequest);
        document.setNamaFile(request.namaFile);
        document.setFileUrl(request.fileUrl);
        document.setJenisDokumen(request.jenisDokumen);
        return documentRepository.save(document);
    }

    public List<ServiceHistory> getHistory(Long id) {
        ServiceRequest serviceRequest = findById(id);
        return historyRepository.findByServiceRequestIdRequest(serviceRequest.getIdRequest());
    }

    private void addHistory(ServiceRequest serviceRequest, String keterangan) {
        ServiceHistory history = new ServiceHistory();
        history.setServiceRequest(serviceRequest);
        history.setKeterangan(keterangan);
        historyRepository.save(history);
    }
}
