package com.innov8ors.insurance.controller;

import com.innov8ors.insurance.enums.NotificationStatus;
import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.response.NotificationPaginatedResponse;
import com.innov8ors.insurance.response.NotificationResponse;
import com.innov8ors.insurance.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.innov8ors.insurance.entity.UserPrincipal;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET /notifications/{userId}
    // Fetch all notifications for a user
    // using @AuthenticationPrincipal to get the user ID from the security context
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<NotificationPaginatedResponse> getAllNotifications(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                          @RequestParam(required = false) Optional<NotificationStatus> notificationStatus,
                                                                          @RequestParam(required = false) Optional<NotificationType> notificationType,
                                                                          @RequestParam(required = false, defaultValue = "0") Integer page,
                                                                          @RequestParam(required = false, defaultValue = "10") Integer size) {
        NotificationPaginatedResponse notifications = notificationService.getNotificationsByUserId(userPrincipal.getId(), notificationStatus.orElse(null), notificationType.orElse(null), page, size);
        return ResponseEntity.ok(notifications);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.markNotificationAsRead(notificationId, userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.markAllNotificationsAsRead(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }
}
