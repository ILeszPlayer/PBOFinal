package com.example.smartcommunity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterCitizenRequest {

    @NotBlank(message = "Nama wajib diisi")
    private String nama;

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "NIK wajib diisi")
    @Size(min = 16, max = 16, message = "NIK harus 16 digit")
    private String nik;

    @NotBlank(message = "Alamat wajib diisi")
    private String alamat;

    @NotBlank(message = "Nomor telepon wajib diisi")
    private String nomorTelepon;

    private String tanggalLahir;

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }
    public String getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(String tanggalLahir) { this.tanggalLahir = tanggalLahir; }
}
