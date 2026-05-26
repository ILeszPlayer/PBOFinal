package com.example.smartcommunity.config;

import com.example.smartcommunity.model.*;
import com.example.smartcommunity.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;
    private final ComplaintRepository complaintRepository;
    private final BroadcastRepository broadcastRepository;
    private final PollRepository pollRepository;
    private final CommentRepository commentRepository;

    public DataInitializer(PenggunaRepository penggunaRepository,
                           PasswordEncoder passwordEncoder,
                           ComplaintRepository complaintRepository,
                           BroadcastRepository broadcastRepository,
                           PollRepository pollRepository,
                           CommentRepository commentRepository) {
        this.penggunaRepository = penggunaRepository;
        this.passwordEncoder = passwordEncoder;
        this.complaintRepository = complaintRepository;
        this.broadcastRepository = broadcastRepository;
        this.pollRepository = pollRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void run(String... args) {
        try {
            if (penggunaRepository.count() > 0) {
                log.info("Database already seeded ({} users). Skipping initialization.", penggunaRepository.count());
                return;
            }

            Admin admin = new Admin("Admin", "admin@gmail.com",
                    passwordEncoder.encode("admin123"));
            admin.setReputationPoints(500);
            penggunaRepository.save(admin);
            log.info("Admin created: admin@gmail.com / admin123");

            Warga warga1 = new Warga("Warga Baru", "warga@gmail.com",
                    passwordEncoder.encode("warga123"));
            warga1.setReputationPoints(75);
            penggunaRepository.save(warga1);
            log.info("Citizen created: warga@gmail.com / warga123");

            Warga warga2 = new Warga("Siti Rahmawati", "siti@gmail.com",
                    passwordEncoder.encode("siti123"));
            warga2.setReputationPoints(120);
            penggunaRepository.save(warga2);

            Warga warga3 = new Warga("Budi Santoso", "budi@gmail.com",
                    passwordEncoder.encode("budi123"));
            warga3.setReputationPoints(45);
            penggunaRepository.save(warga3);

            Warga warga4 = new Warga("Dewi Lestari", "dewi@gmail.com",
                    passwordEncoder.encode("dewi123"));
            warga4.setReputationPoints(210);
            penggunaRepository.save(warga4);
            log.info("Additional citizens seeded.");

            // Seed sample complaints
            Complaint c1 = new Complaint();
            c1.setJudul("Jalan Berlubang di Depan Gang Mawar");
            c1.setIsiPengaduan("Jalan di depan Gang Mawar sudah berlubang selama 2 minggu. Sangat membahayakan pengendara motor. Mohon segera diperbaiki.");
            c1.setKategori(Complaint.Kategori.INFRASTRUKTUR);
            c1.setUser(warga1);
            c1.setStatus(Complaint.Status.PROSES);
            c1.setUrgency(Complaint.Urgency.TINGGI);
            c1.setUpvotesCount(12);
            c1.setTanggal(LocalDateTime.now().minusDays(5));
            c1.setProcessedAt(LocalDateTime.now().minusDays(3));
            complaintRepository.save(c1);

            Complaint c2 = new Complaint();
            c2.setJudul("Tumpukan Sampah di Pasar Induk");
            c2.setIsiPengaduan("Sampah di belakang Pasar Induk sudah menumpuk selama seminggu dan menimbulkan bau tidak sedap.");
            c2.setKategori(Complaint.Kategori.LINGKUNGAN);
            c2.setUser(warga2);
            c2.setStatus(Complaint.Status.SELESAI);
            c2.setUrgency(Complaint.Urgency.SEDANG);
            c2.setUpvotesCount(8);
            c2.setTanggal(LocalDateTime.now().minusDays(10));
            c2.setProcessedAt(LocalDateTime.now().minusDays(7));
            c2.setResolvedAt(LocalDateTime.now().minusDays(6));
            complaintRepository.save(c2);

            Complaint c3 = new Complaint();
            c3.setJudul("Lampu Jalan Mati di RT 03");
            c3.setIsiPengaduan("Lampu jalan di area RT 03 sudah mati selama 3 hari. Jalan menjadi gelap dan rawan kejahatan.");
            c3.setKategori(Complaint.Kategori.KEAMANAN);
            c3.setUser(warga3);
            c3.setStatus(Complaint.Status.PENDING);
            c3.setUrgency(Complaint.Urgency.TINGGI);
            c3.setUpvotesCount(15);
            c3.setTanggal(LocalDateTime.now().minusDays(1));
            complaintRepository.save(c3);

            Complaint c4 = new Complaint();
            c4.setJudul("Saluran Air Tersumbat");
            c4.setIsiPengaduan("Selokan di depan rumah warga tersumbat dan menyebabkan genangan air saat hujan.");
            c4.setKategori(Complaint.Kategori.INFRASTRUKTUR);
            c4.setUser(warga4);
            c4.setStatus(Complaint.Status.PROSES);
            c4.setUrgency(Complaint.Urgency.SEDANG);
            c4.setUpvotesCount(5);
            c4.setTanggal(LocalDateTime.now().minusDays(3));
            c4.setProcessedAt(LocalDateTime.now().minusDays(1));
            complaintRepository.save(c4);

            Complaint c5 = new Complaint();
            c5.setJudul("Kegiatan Posyandu Kurang Sosialisasi");
            c5.setIsiPengaduan("Warga di RW 05 kurang mendapat informasi jadwal posyandu bulan ini. Mohon dijadwalkan ulang.");
            c5.setKategori(Complaint.Kategori.KESEHATAN);
            c5.setUser(warga1);
            c5.setStatus(Complaint.Status.SELESAI);
            c5.setUrgency(Complaint.Urgency.RENDAH);
            c5.setUpvotesCount(3);
            c5.setTanggal(LocalDateTime.now().minusDays(15));
            c5.setProcessedAt(LocalDateTime.now().minusDays(12));
            c5.setResolvedAt(LocalDateTime.now().minusDays(10));
            complaintRepository.save(c5);
            log.info("Sample complaints seeded.");

            // Seed comments
            Comment comment1 = new Comment("Setuju, jalan ini sangat membahayakan. Tolong segera ditindaklanjuti.", warga2, c1);
            commentRepository.save(comment1);
            Comment comment2 = new Comment("Saya juga sering lewat sini, semoga cepat diperbaiki.", warga3, c1);
            commentRepository.save(comment2);
            Comment comment3 = new Comment("Alhamdulillah sampah sudah diangkut. Terima kasih.", warga1, c2);
            commentRepository.save(comment3);
            log.info("Sample comments seeded.");

            // Seed broadcasts
            Broadcast b1 = new Broadcast(
                    "Informasi Kerja Bakti",
                    "Kerja bakti lingkungan akan dilaksanakan pada hari Minggu, 25 Mei 2026 pukul 07.00 WIB. " +
                    "Diharapkan partisipasi seluruh warga RW 05.",
                    admin
            );
            broadcastRepository.save(b1);

            Broadcast b2 = new Broadcast(
                    "Pembayaran Iuran Sampah",
                    "Pengingat: Pembayaran iuran sampah bulan Mei 2026 paling lambat tanggal 30 Mei 2026. " +
                    "Bayar di ketua RT masing-masing.",
                    admin
            );
            broadcastRepository.save(b2);
            log.info("Sample broadcasts seeded.");

            // Seed polls
            Poll poll1 = new Poll("Setujukah Anda dengan diadakannya kerja bakti setiap hari Minggu?", admin);
            pollRepository.save(poll1);

            Poll poll2 = new Poll("Apakah Anda setuju pembangunan taman bermain di area RW 05?", admin);
            pollRepository.save(poll2);
            log.info("Sample polls seeded.");

            log.info("Database initialization complete!");

        } catch (Exception e) {
            log.error("DataInitializer failed: {}", e.getMessage());
        }
    }
}
