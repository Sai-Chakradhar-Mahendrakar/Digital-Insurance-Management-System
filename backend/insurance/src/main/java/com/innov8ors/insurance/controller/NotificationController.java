package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.response.NotificationResponse;
import com.innov8ors.insurance.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.innov8ors.insurance.entity.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userPrincipal.getId());
        return ResponseEntity.ok(notifications);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("admin/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @RequestBody NotificationSendRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.markNotificationAsRead(notificationId, userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/user/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.markAllNotificationsAsRead(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }
}
