package com.example.smartcommunity.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GENERAL")
public class GeneralServiceRequest extends ServiceRequest {
    @Override
    public String processRequest() {
        setStatus("DIPROSES");
        return "Pengajuan layanan umum sedang diproses";
    }
}
