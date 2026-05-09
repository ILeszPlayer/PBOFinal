package com.example.smartcommunity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "service_documents")
public class ServiceDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;
    private String namaFile;
    private String fileUrl;
    private String jenisDokumen;

    @ManyToOne
    @JoinColumn(name = "id_request")
    @JsonIgnore
    private ServiceRequest serviceRequest;

    public Long getIdDocument() { return idDocument; }
    public void setIdDocument(Long idDocument) { this.idDocument = idDocument; }
    public String getNamaFile() { return namaFile; }
    public void setNamaFile(String namaFile) { this.namaFile = namaFile; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getJenisDokumen() { return jenisDokumen; }
    public void setJenisDokumen(String jenisDokumen) { this.jenisDokumen = jenisDokumen; }
    public ServiceRequest getServiceRequest() { return serviceRequest; }
    public void setServiceRequest(ServiceRequest serviceRequest) { this.serviceRequest = serviceRequest; }
}
