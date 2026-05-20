package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateCommentRequest;
import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.Citizen;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.CommentService;
import com.example.smartcommunity.service.ComplaintService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CitizenController {

    private final ComplaintService complaintService;
    private final BroadcastService broadcastService;
    private final CommentService commentService;

    public CitizenController(ComplaintService complaintService,
                             BroadcastService broadcastService,
                             CommentService commentService) {
        this.complaintService = complaintService;
        this.broadcastService = broadcastService;
        this.commentService = commentService;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        if (!isCitizen(session)) return "redirect:/login";

        model.addAttribute("broadcasts", broadcastService.getAllBroadcasts());
        model.addAttribute("complaints", complaintService.getAllComplaints());
        model.addAttribute("loggedUserName", session.getAttribute("loggedUserName"));
        model.addAttribute("loggedUserId", session.getAttribute("loggedUserId"));
        return "citizen-home";
    }

    @PostMapping("/complaint/create")
    public String createComplaint(@ModelAttribute CreateComplaintRequest request,
                                  HttpSession session) {
        if (!isCitizen(session)) return "redirect:/login";

        request.setUserId((Long) session.getAttribute("loggedUserId"));
        complaintService.createComplaint(request);
        return "redirect:/home";
    }

    @PostMapping("/comment/add")
    public String addComment(@RequestParam Long complaintId,
                             @RequestParam String isiKomentar,
                             HttpSession session) {
        if (!isCitizen(session)) return "redirect:/login";

        CreateCommentRequest request = new CreateCommentRequest();
        request.setUserId((Long) session.getAttribute("loggedUserId"));
        request.setComplaintId(complaintId);
        request.setIsiKomentar(isiKomentar);
        commentService.addComment(request);
        return "redirect:/home";
    }

    private boolean isCitizen(HttpSession session) {
        return session.getAttribute("loggedUser") instanceof Citizen;
    }
}
