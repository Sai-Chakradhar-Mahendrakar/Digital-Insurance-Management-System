package com.innov8ors.insurance.service.impl;

import com.innov8ors.insurance.entity.Notification;
import com.innov8ors.insurance.entity.User;
import com.innov8ors.insurance.enums.NotificationStatus;
import com.innov8ors.insurance.enums.NotificationType;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.mapper.NotificationMapper;
import com.innov8ors.insurance.repository.dao.NotificationDao;
import com.innov8ors.insurance.request.NotificationByPolicyRequest;
import com.innov8ors.insurance.request.NotificationRequest;
import com.innov8ors.insurance.request.NotificationSendBulkRequest;
import com.innov8ors.insurance.request.NotificationSendRequest;
import com.innov8ors.insurance.response.NotificationPaginatedResponse;
import com.innov8ors.insurance.response.NotificationResponse;
import com.innov8ors.insurance.response.UserPolicyPaginatedResponse;
import com.innov8ors.insurance.response.UserPolicyResponse;
import com.innov8ors.insurance.service.NotificationService;
import com.innov8ors.insurance.service.UserPolicyService;
import com.innov8ors.insurance.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.innov8ors.insurance.util.Constant.ErrorMessage.NOTIFICATION_NOT_FOUND_OR_DOES_NOT_BELONG_TO_USER;
import static com.innov8ors.insurance.util.Constant.NotificationConstants.ID_PLACEHOLDER;
import static com.innov8ors.insurance.util.Constant.NotificationConstants.NOTIFICATION_CREATED_AT_PLACEHOLDER;
import static com.innov8ors.insurance.util.Constant.NotificationConstants.NOTIFICATION_STATUS_PLACEHOLDER;
import static com.innov8ors.insurance.util.Constant.NotificationConstants.NOTIFICATION_TYPE_PLACEHOLDER;
import static com.innov8ors.insurance.util.Constant.NotificationConstants.USER_PLACEHOLDER;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDao notificationDao;
    private final UserService userService;
    private final UserPolicyService userPolicyService;

    public NotificationServiceImpl(NotificationDao notificationDao, UserService userService, UserPolicyService userPolicyService) {
        this.notificationDao = notificationDao;
        this.userService = userService;
        this.userPolicyService = userPolicyService;
    }

    @Override
    public void markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = checkAndGetNotification(notificationId, userId);
        markNotificationAsReadAndUpdateReadAt(notification);
        notificationDao.persist(notification);
    }

    private void markNotificationAsReadAndUpdateReadAt(Notification notification) {
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
    }

    private Notification checkAndGetNotification(Long notificationId, Long userId) {
        Optional<Notification> optionalNotification = notificationDao.getByIdAndUserId(notificationId, userId);
        if (optionalNotification.isEmpty()) {
            throw new NotFoundException(NOTIFICATION_NOT_FOUND_OR_DOES_NOT_BELONG_TO_USER);
        }
        return optionalNotification.get();
    }

    @Override
    public NotificationPaginatedResponse getNotificationsByUserId(Long userId, NotificationStatus notificationStatus, NotificationType notificationType, Integer page, Integer size) {
        Page<Notification> notifications = notificationDao.findAll(getNotificationQuery(userId, notificationStatus, notificationType), PageRequest.of(page, size, Sort.by(NOTIFICATION_CREATED_AT_PLACEHOLDER).descending()));
        return NotificationMapper.getNotificationPaginatedResponse(notifications, page, size);
    }

    private Specification<Notification> getNotificationQuery(Long userId, NotificationStatus notificationStatus, NotificationType notificationType) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get(USER_PLACEHOLDER).get(ID_PLACEHOLDER), userId));
            if (notificationStatus != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get(NOTIFICATION_STATUS_PLACEHOLDER), notificationStatus));
            }
            if (notificationType != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get(NOTIFICATION_TYPE_PLACEHOLDER), notificationType));
            }
            return predicates;
        };
    }

    @Override
    public void sendNotificationsBulk(NotificationSendBulkRequest request) {
        for (Long userId : request.getUserId()) {
            NotificationRequest notificationRequest = request.getRequest();
            sendNotification(userId, notificationRequest.getMessage(), notificationRequest.getType());
        }
    }

    @Override
    public NotificationResponse sendNotification(Long userId, String message, NotificationType type) {
        User user = userService.getById(userId);
        NotificationSendRequest request = NotificationSendRequest.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .build();
        Notification notification = getNotificationFromRequest(request, user);
        Notification saved = notificationDao.persist(notification);
        return NotificationMapper.toNotificationResponse(saved);
    }

    private Notification getNotificationFromRequest(NotificationSendRequest request, User user) {
        return Notification.builder()
                .user(user)
                .message(request.getMessage())
                .type(request.getType())
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .readAt(null)
                .build();
    }

    @Override
    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> notifications = notificationDao.findByUserIdAndStatus(userId, NotificationStatus.UNREAD);
        for (Notification notification : notifications) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
        }
        notificationDao.saveAll(notifications);
    }

    @Override
    public void sendNotificationByPolicy(NotificationByPolicyRequest request) {
        UserPolicyPaginatedResponse usersWithPolicy = userPolicyService.getUsersByPolicyId(request.getPolicyId(), 0, Integer.MAX_VALUE);
        List<Long> userIds = usersWithPolicy.getUserPolicies()
                .stream()
                .map(UserPolicyResponse::getUserId)
                .toList();
        NotificationSendBulkRequest notificationSendBulkRequest = NotificationSendBulkRequest.builder()
                .userId(userIds)
                .request(request.getRequest())
                .build();
        sendNotificationsBulk(notificationSendBulkRequest);
    }
}
