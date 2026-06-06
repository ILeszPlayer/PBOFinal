package com.example.smartcommunity.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Pengguna {

    private String levelAkses = "SUPER_ADMIN";

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Broadcast> broadcasts = new ArrayList<>();

    public Admin() {
        setRole("ADMIN");
    }

    public Admin(String nama, String email, String password) {
        setNama(nama);
        setEmail(email);
        setPassword(password);
        setRole("ADMIN");
    }

    @Override
    public String getDashboardRoute() {
        return "/admin/dashboard";
    }

    @Override
    public String getRoleDisplayName() {
        return "Admin";
    }

    @Override
    public String getSummary() {
        return getNama() + " (Admin - " + getLevelAkses() + ")";
    }

    public String getLevelAkses() { return levelAkses; }
    public void setLevelAkses(String levelAkses) { this.levelAkses = levelAkses; }
    public List<Broadcast> getBroadcasts() { return broadcasts; }
    public void setBroadcasts(List<Broadcast> broadcasts) { this.broadcasts = broadcasts; }
}
