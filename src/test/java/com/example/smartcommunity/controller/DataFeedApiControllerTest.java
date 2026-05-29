package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Warga;
import com.example.smartcommunity.repository.CommentRepository;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataFeedApiController.class)
class DataFeedApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ComplaintRepository complaintRepository;
    @MockBean private CommentRepository commentRepository;
    @MockBean private PenggunaRepository penggunaRepository;
    @MockBean private NotificationService notificationService;

    private Complaint createMockComplaint(Long id) {
        Complaint c = new Complaint();
        c.setId(id);
        c.setJudul("Test Judul");
        c.setIsiPengaduan("Test Isi");
        c.setKategori(Complaint.Kategori.UMUM);
        c.setStatus(Complaint.Status.PENDING);
        c.setUrgency(Complaint.Urgency.RENDAH);
        c.setTanggal(java.time.LocalDateTime.now());
        c.setUpvotesCount(0);
        c.setIsAnonymous(false);
        return c;
    }

    @Test
    @WithMockUser
    void getComplaintsAsync_ReturnsPage() throws Exception {
        Complaint c = createMockComplaint(1L);
        Warga w = new Warga();
        w.setId(1L);
        w.setNama("Test User");
        c.setUser(w);
        Page<Complaint> page = new PageImpl<>(List.of(c));
        when(complaintRepository.findAllByOrderByUpvotesCountDesc(any(Pageable.class))).thenReturn(page);
        when(commentRepository.findByComplaintIdOrderByTanggalAsc(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/feed/complaints?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser
    void getComplaint_ReturnsComplaint() throws Exception {
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(createMockComplaint(1L)));
        when(commentRepository.findCommentRawDataByComplaintId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/complaints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getComplaint_NotFound_ReturnsError() throws Exception {
        when(complaintRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/complaints/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Pengaduan tidak ditemukan")));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void upvoteComplaint_Increments() throws Exception {
        Complaint c = createMockComplaint(1L);
        c.setUpvotesCount(5);
        Warga user = new Warga();
        user.setId(1L);
        user.setEmail("test@test.com");
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(c));
        when(penggunaRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/complaints/1/upvote").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(6));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void addComment_ReturnsSuccess() throws Exception {
        Complaint c = createMockComplaint(1L);
        Warga user = new Warga();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setNama("Test");

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(c));
        when(penggunaRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(commentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/complaints/1/comment").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isiKomentar\":\"Test komentar\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
