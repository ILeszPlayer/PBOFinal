package com.example.smartcommunity.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CITIZEN")
public class Citizen extends User {

    public Citizen() {}

    public Citizen(String nama, String email, String password) {
        setNama(nama);
        setEmail(email);
        setPassword(password);
    }
}
