package com.example.smartcommunity.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    public String verifikasiLayanan() { return "Admin memverifikasi layanan"; }
    public String updateStatus() { return "Admin memperbarui status"; }
    public String kelolaPengaduan() { return "Admin mengelola pengaduan"; }
    public String lihatDashboard() { return "Admin melihat dashboard"; }
}
