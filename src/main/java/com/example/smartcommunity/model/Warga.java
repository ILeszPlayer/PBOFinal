package com.example.smartcommunity.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CITIZEN")
public class Warga extends Pengguna {

    public Warga() {
        setRole("CITIZEN");
    }

    public Warga(String nama, String email, String password) {
        setNama(nama);
        setEmail(email);
        setPassword(password);
        setRole("CITIZEN");
    }

    @Override
    public String getDashboardRoute() {
        return "/citizen/home";
    }

    @Override
    public String getRoleDisplayName() {
        return "Warga";
    }

    @Override
    public String getSummary() {
        return getNama() + " - " + getReputationTier() + " (" + getReputationPoints() + " poin)";
    }
}
