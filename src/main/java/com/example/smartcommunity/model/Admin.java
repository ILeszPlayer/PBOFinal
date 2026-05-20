package com.example.smartcommunity.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

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

    public List<Broadcast> getBroadcasts() { return broadcasts; }
    public void setBroadcasts(List<Broadcast> broadcasts) { this.broadcasts = broadcasts; }
}
