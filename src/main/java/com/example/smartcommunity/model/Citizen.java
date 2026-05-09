package com.example.smartcommunity.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CITIZEN")
public class Citizen extends User {
    public String buatPengaduan() { return "Masyarakat membuat pengaduan"; }
    public String ajukanLayanan() { return "Masyarakat mengajukan layanan"; }
    public String lihatRiwayat() { return "Masyarakat melihat riwayat"; }
}
