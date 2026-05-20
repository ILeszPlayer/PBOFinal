package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateCommentRequest;
import com.example.smartcommunity.dto.CreateComplaintRequest;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.service.BroadcastService;
import com.example.smartcommunity.service.CommentService;
import com.example.smartcommunity.service.ComplaintService;
import com.example.smartcommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CitizenController {

    private final ComplaintService complaintService;
    private final BroadcastService broadcastService;
    private final CommentService commentService;
    private final UserService userService;

    public CitizenController(ComplaintService complaintService,
                             BroadcastService broadcastService,
                             CommentService commentService,
                             UserService userService) {
        this.complaintService = complaintService;
        this.broadcastService = broadcastService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping({"/", "/home"})
    public String home(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("loggedUserId");
        if (userId == null) return "redirect:/login";

        User citizen = userService.findById(userId);
        model.addAttribute("broadcasts", broadcastService.getAllBroadcasts());
        model.addAttribute("complaints", complaintService.getAllComplaints());
        model.addAttribute("loggedUserName", citizen.getNama());
        model.addAttribute("loggedUserId", citizen.getId());
        return "citizen-home";
    }

    @PostMapping("/complaint/create")
    public String createComplaint(CreateComplaintRequest request,
                                  HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedUserId");
        if (userId == null) return "redirect:/login";

        request.setUserId(userId);
        complaintService.createComplaint(request);
        return "redirect:/home";
    }

    @PostMapping("/complaint/{id}/upvote")
    public String upvoteComplaint(@PathVariable Long id) {
        complaintService.upvote(id);
        return "redirect:/home";
    }

    @PostMapping("/comment/add")
    public String addComment(@RequestParam Long complaintId,
                             @RequestParam String isiKomentar,
                             HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedUserId");
        if (userId == null) return "redirect:/login";

        CreateCommentRequest request = new CreateCommentRequest();
        request.setUserId(userId);
        request.setComplaintId(complaintId);
        request.setIsiKomentar(isiKomentar);
        commentService.addComment(request);
        return "redirect:/home";
    }
}
