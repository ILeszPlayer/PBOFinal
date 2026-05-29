package com.example.smartcommunity.controller;

import com.example.smartcommunity.repository.CommentRepository;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.repository.PollRepository;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.PenggunaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminWebController.class)
class AdminWebControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ComplaintRepository complaintRepository;
    @MockBean private BroadcastService broadcastService;
    @MockBean private PenggunaService userService;
    @MockBean private PenggunaRepository penggunaRepository;
    @MockBean private NotificationService notificationService;
    @MockBean private CommentRepository commentRepository;
    @MockBean private PollRepository pollRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewDashboardMetrics_ReturnsDashboard() throws Exception {
        when(complaintRepository.count()).thenReturn(10L);
        when(complaintRepository.countByStatus("PENDING")).thenReturn(3L);
        when(complaintRepository.countByStatus("PROSES")).thenReturn(4L);
        when(complaintRepository.countByStatus("SELESAI")).thenReturn(3L);
        when(complaintRepository.getMonthlyStats()).thenReturn(new ArrayList<>());
        when(complaintRepository.findAllByOrderByTanggalDesc()).thenReturn(new ArrayList<>());
        when(complaintRepository.findAllByOrderByUpvotesCountDesc()).thenReturn(new ArrayList<>());
        when(complaintRepository.findByStatus(com.example.smartcommunity.model.Complaint.Status.SELESAI)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-dashboard"))
                .andExpect(model().attributeExists("totalComplaints"))
                .andExpect(model().attribute("totalComplaints", 10L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void manageComplaintsMenu_ReturnsPage() throws Exception {
        when(complaintRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/complaints/manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-manage-complaints"))
                .andExpect(model().attributeExists("complaints"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewWargaMenu_ReturnsPage() throws Exception {
        when(userService.findAllCitizens()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/warga"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewBroadcasts_ReturnsPage() throws Exception {
        when(broadcastService.getAllBroadcasts()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/broadcasts"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-broadcasts"))
                .andExpect(model().attributeExists("broadcasts"));
    }
}
