package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Warga;
import com.example.smartcommunity.repository.ComplaintRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.impl.ComplaintServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceTest {

    @Mock private ComplaintRepository complaintRepository;
    @Mock private PenggunaRepository penggunaRepository;
    @Mock private PenggunaService penggunaService;
    @Mock private NotificationService notificationService;

    private ComplaintServiceImpl complaintService;

    @BeforeEach
    void setUp() {
        complaintService = new ComplaintServiceImpl(
                complaintRepository, penggunaRepository,
                penggunaService, notificationService);
    }

    @Test
    void createComplaint_AwardsPointsAndNotifies() {
        Warga user = new Warga();
        user.setId(1L);
        when(penggunaRepository.findById(1L)).thenReturn(Optional.of(user));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> {
            Complaint c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        CreateComplaintRequest req = new CreateComplaintRequest();
        req.setUserId(1L);
        req.setJudul("Jalan berlubang");
        req.setIsiPengaduan("Ada jalan berlubang di depan rumah");

        Complaint result = complaintService.createComplaint(req);

        assertNotNull(result);
        assertEquals(Complaint.Kategori.INFRASTRUKTUR, result.getKategori());
        assertEquals(Complaint.Status.PENDING, result.getStatus());
        verify(penggunaService).awardPoints(1L, 10);
        verify(notificationService).createNotification(any(), anyString(), eq(user), any(Complaint.class));
    }

    @Test
    void updateStatus_ChangesStatusAndNotifies() {
        Warga user = new Warga();
        user.setId(1L);
        Complaint complaint = new Complaint();
        complaint.setId(1L);
        complaint.setUser(user);
        complaint.setJudul("Test");
        complaint.setStatus(Complaint.Status.PENDING);
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));

        Complaint result = complaintService.updateStatus(1L, Complaint.Status.PROSES);

        assertEquals(Complaint.Status.PROSES, result.getStatus());
        assertNotNull(result.getProcessedAt());
        verify(notificationService).createNotification(any(), anyString(), eq(user), any(Complaint.class));
    }

    @Test
    void upvote_IncrementsCount() {
        Complaint complaint = new Complaint();
        complaint.setId(1L);
        complaint.setUpvotesCount(5);
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));

        Complaint result = complaintService.upvote(1L);

        assertEquals(6, result.getUpvotesCount());
    }

    @Test
    void deleteComplaint_DeletesWhenExists() {
        when(complaintRepository.existsById(1L)).thenReturn(true);

        complaintService.deleteComplaint(1L);

        verify(complaintRepository).deleteById(1L);
    }

    @Test
    void deleteComplaint_ThrowsWhenNotFound() {
        when(complaintRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> complaintService.deleteComplaint(99L));
    }

    @Test
    void searchComplaints_WithKeyword_CallsRepo() {
        String keyword = "jalan";
        when(complaintRepository.findByJudulContainingIgnoreCaseOrIsiPengaduanContainingIgnoreCase(keyword, keyword))
                .thenReturn(List.of());

        List<Complaint> result = complaintService.searchComplaints(keyword);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchComplaints_WithEmptyKeyword_ReturnsAll() {
        when(complaintRepository.findAllByOrderByTanggalDesc()).thenReturn(List.of(new Complaint()));

        List<Complaint> result = complaintService.searchComplaints("");

        assertEquals(1, result.size());
    }

    @Test
    void countAll_ReturnsCount() {
        when(complaintRepository.count()).thenReturn(10L);

        assertEquals(10, complaintService.countAll());
    }
}
