package com.example.smartcommunity.service;

import com.example.smartcommunity.model.Broadcast;
import com.example.smartcommunity.model.Warga;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.repository.BroadcastRepository;
import com.example.smartcommunity.repository.PenggunaRepository;
import com.example.smartcommunity.service.impl.BroadcastServiceImpl;
import com.example.smartcommunity.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BroadcastServiceTest {

    @Mock private BroadcastRepository broadcastRepository;
    @Mock private PenggunaRepository penggunaRepository;
    @Mock private NotificationService notificationService;

    private BroadcastServiceImpl broadcastService;

    @BeforeEach
    void setUp() {
        broadcastService = new BroadcastServiceImpl(broadcastRepository, penggunaRepository, notificationService);
    }

    @Test
    void getAllBroadcasts_ReturnsList() {
        Broadcast b = new Broadcast("Judul", "Isi", new Warga());
        b.setId(1L);
        when(broadcastRepository.findAllByOrderByTanggalDesc()).thenReturn(List.of(b));

        List<Broadcast> result = broadcastService.getAllBroadcasts();

        assertEquals(1, result.size());
        assertEquals("Judul", result.get(0).getJudul());
    }

    @Test
    void getAllBroadcasts_WhenNull_ReturnsEmptyList() {
        when(broadcastRepository.findAllByOrderByTanggalDesc()).thenReturn(null);

        List<Broadcast> result = broadcastService.getAllBroadcasts();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllBroadcasts_WhenException_ReturnsEmptyList() {
        when(broadcastRepository.findAllByOrderByTanggalDesc()).thenThrow(new RuntimeException());

        List<Broadcast> result = broadcastService.getAllBroadcasts();

        assertTrue(result.isEmpty());
    }

    @Test
    void saveBroadcast_SetsDateAndAdmin() {
        Warga admin = new Warga();
        admin.setId(1L);
        admin.setRole("ADMIN");
        Broadcast b = new Broadcast("Judul", "Isi", null);
        when(penggunaRepository.findAll()).thenReturn(List.of(admin));
        when(broadcastRepository.save(any(Broadcast.class))).thenAnswer(i -> i.getArgument(0));

        Broadcast result = broadcastService.save(b);

        assertNotNull(result.getTanggal());
        assertNotNull(result.getAdmin());
    }

    @Test
    void updateBroadcast_UpdatesFields() {
        Warga admin = new Warga();
        admin.setId(1L);
        Broadcast existing = new Broadcast("Old", "Old isi", admin);
        existing.setId(1L);
        existing.setTanggal(LocalDateTime.now());
        when(broadcastRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(broadcastRepository.save(any(Broadcast.class))).thenAnswer(i -> i.getArgument(0));

        Broadcast result = broadcastService.updateBroadcast(1L, "New Judul", "New Isi");

        assertEquals("New Judul", result.getJudul());
        assertEquals("New Isi", result.getIsiBroadcast());
    }

    @Test
    void updateBroadcast_ThrowsWhenNotFound() {
        when(broadcastRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> broadcastService.updateBroadcast(99L, "J", "I"));
    }

    @Test
    void deleteBroadcast_DeletesWhenExists() {
        when(broadcastRepository.existsById(1L)).thenReturn(true);

        broadcastService.deleteBroadcast(1L);

        verify(broadcastRepository).deleteById(1L);
    }

    @Test
    void deleteBroadcast_ThrowsWhenNotFound() {
        when(broadcastRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> broadcastService.deleteBroadcast(99L));
    }

    @Test
    void findById_ReturnsBroadcast() {
        Broadcast b = new Broadcast("J", "I", new Warga());
        b.setId(1L);
        when(broadcastRepository.findById(1L)).thenReturn(Optional.of(b));

        Broadcast result = broadcastService.findById(1L);

        assertEquals("J", result.getJudul());
    }

    @Test
    void findById_ThrowsWhenNotFound() {
        when(broadcastRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> broadcastService.findById(99L));
    }
}
