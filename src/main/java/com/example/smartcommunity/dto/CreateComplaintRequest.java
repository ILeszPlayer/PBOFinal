package com.example.smartcommunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class CreateComplaintRequest {

    @NotNull(message = "User ID wajib diisi")
    private Long userId;

    @NotBlank(message = "Judul wajib diisi")
    private String judul;

    @NotBlank(message = "Isi pengaduan wajib diisi")
    private String isiPengaduan;

    @NotNull(message = "Kategori wajib dipilih")
    private String kategori;

    private MultipartFile buktiFoto;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getIsiPengaduan() { return isiPengaduan; }
    public void setIsiPengaduan(String isiPengaduan) { this.isiPengaduan = isiPengaduan; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public MultipartFile getBuktiFoto() { return buktiFoto; }
    public void setBuktiFoto(MultipartFile buktiFoto) { this.buktiFoto = buktiFoto; }
}
