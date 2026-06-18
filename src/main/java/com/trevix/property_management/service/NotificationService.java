package com.trevix.property_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.entity.Notification;
import com.trevix.property_management.entity.User;
import com.trevix.property_management.repository.NotificationRepository;
import com.trevix.property_management.repository.UserRepository;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.exception.AppException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // ─── Core persistence ────────────────────────────────────────────────────

    private Notification persist(User user, String type, String title, String body, String data) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setData(data);
        notification.setIsRead(false);
        return notificationRepository.save(notification);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found: " + userId));
    }

    // ─── Channel dispatchers (wire real providers here) ───────────────────────

    private void sendEmail(String email, String subject, String body) {
        // TODO: JavaMailSender / SendGrid
        log.info("[EMAIL] To: {} | Subject: {}", email, subject);
    }

    private void sendSms(String phone, String message) {
        // TODO: Twilio
        log.info("[SMS] To: {} | Message: {}", phone, message);
    }

    private void sendPush(String fcmToken, String title, String body) {
        // TODO: Firebase FCM
        log.info("[PUSH] Token: {} | Title: {}", fcmToken, title);
    }

    // ─── Auth ─────────────────────────────────────────────────────────────────

    public void sendWelcomeEmail(String email, String fullName) {
        String subject = "Welcome to Trevix!";
        String body = "Hi " + fullName + ", your account has been created successfully.";
        sendEmail(email, subject, body);
        log.info("Welcome email sent to: {}", email);
    }

    public void sendPasswordResetEmail(String email, String resetToken) {
        String subject = "Reset Your Password";
        String body = "Use this token to reset your password: " + resetToken;
        sendEmail(email, subject, body);
        log.info("Password reset email sent to: {}", email);
    }

    // ─── Billing ──────────────────────────────────────────────────────────────

    public void notifyRentDue(UUID userId, String roomNumber, String amount, String dueDate) {
        User user = findUser(userId);
        String title = "Rent Due";
        String body = "Your rent of " + amount + " for room " + roomNumber + " is due on " + dueDate + ".";
        String data = "{\"roomNumber\":\"" + roomNumber + "\",\"amount\":\"" + amount + "\",\"dueDate\":\"" + dueDate + "\"}";

        persist(user, "RENT_DUE", title, body, data);
        sendEmail(user.getEmail(), title, body);
        sendSms(user.getPhone(), body);
        log.info("Rent due notification sent to user: {}", userId);
    }

    public void notifyPaymentConfirmed(UUID userId, String roomNumber, String amount, String referenceNo) {
        User user = findUser(userId);
        String title = "Payment Confirmed";
        String body = "Payment of " + amount + " for room " + roomNumber + " confirmed. Ref: " + referenceNo;
        String data = "{\"roomNumber\":\"" + roomNumber + "\",\"amount\":\"" + amount + "\",\"referenceNo\":\"" + referenceNo + "\"}";

        persist(user, "PAYMENT_CONFIRMED", title, body, data);
        sendEmail(user.getEmail(), title, body);
        sendSms(user.getPhone(), body);
        log.info("Payment confirmed notification sent to user: {}", userId);
    }

    // ─── Maintenance ──────────────────────────────────────────────────────────

    public void notifyMaintenanceSubmitted(UUID userId, UUID requestId, String requestTitle) {
        User user = findUser(userId);
        String title = "Maintenance Request Submitted";
        String body = "Your request \"" + requestTitle + "\" has been received and is being reviewed.";
        String data = "{\"requestId\":\"" + requestId + "\"}";

        persist(user, "MAINTENANCE_SUBMITTED", title, body, data);
        sendEmail(user.getEmail(), title, body);
        log.info("Maintenance submitted notification sent to user: {}", userId);
    }

    public void notifyMaintenanceUpdated(UUID userId, UUID requestId, String requestTitle, String status) {
        User user = findUser(userId);
        String title = "Maintenance Request Updated";
        String body = "Your request \"" + requestTitle + "\" status has been updated to: " + status;
        String data = "{\"requestId\":\"" + requestId + "\",\"status\":\"" + status + "\"}";

        persist(user, "MAINTENANCE_UPDATED", title, body, data);
        sendEmail(user.getEmail(), title, body);
        sendPush(null, title, body); // TODO: pass real FCM token from user profile
        log.info("Maintenance updated notification sent to user: {}", userId);
    }

    public void notifyMaintenanceCompleted(UUID userId, UUID requestId, String requestTitle) {
        User user = findUser(userId);
        String title = "Maintenance Completed";
        String body = "Your request \"" + requestTitle + "\" has been completed.";
        String data = "{\"requestId\":\"" + requestId + "\"}";

        persist(user, "MAINTENANCE_COMPLETED", title, body, data);
        sendEmail(user.getEmail(), title, body);
        sendSms(user.getPhone(), body);
        sendPush(null, title, body);
        log.info("Maintenance completed notification sent to user: {}", userId);
    }

    // ─── Lease ────────────────────────────────────────────────────────────────

    public void notifyLeaseExpiringSoon(UUID userId, String roomNumber, String expiryDate, int daysLeft) {
        User user = findUser(userId);
        String title = "Lease Expiring Soon";
        String body = "Your lease for room " + roomNumber + " expires on " + expiryDate + " (" + daysLeft + " days left).";
        String data = "{\"roomNumber\":\"" + roomNumber + "\",\"expiryDate\":\"" + expiryDate + "\",\"daysLeft\":" + daysLeft + "}";

        persist(user, "LEASE_EXPIRING", title, body, data);
        sendEmail(user.getEmail(), title, body);
        sendSms(user.getPhone(), body);
        log.info("Lease expiry notification sent to user: {}", userId);
    }

    public void notifyLeaseExpired(UUID userId, String roomNumber) {
        User user = findUser(userId);
        String title = "Lease Expired";
        String body = "Your lease for room " + roomNumber + " has expired. Please contact your property manager.";
        String data = "{\"roomNumber\":\"" + roomNumber + "\"}";

        persist(user, "LEASE_EXPIRED", title, body, data);
        sendEmail(user.getEmail(), title, body);
        sendSms(user.getPhone(), body);
        log.info("Lease expired notification sent to user: {}", userId);
    }

    public void notifyLeaseRenewed(UUID userId, String roomNumber, String newEndDate) {
        User user = findUser(userId);
        String title = "Lease Renewed";
        String body = "Your lease for room " + roomNumber + " has been renewed until " + newEndDate + ".";
        String data = "{\"roomNumber\":\"" + roomNumber + "\",\"newEndDate\":\"" + newEndDate + "\"}";

        persist(user, "LEASE_RENEWED", title, body, data);
        sendEmail(user.getEmail(), title, body);
        log.info("Lease renewed notification sent to user: {}", userId);
    }

    // ─── Announcements ────────────────────────────────────────────────────────

    public void sendAnnouncement(UUID userId, String announcementTitle, String announcementBody) {
        User user = findUser(userId);
        String data = "{\"announcementTitle\":\"" + announcementTitle + "\"}";

        persist(user, "ANNOUNCEMENT", announcementTitle, announcementBody, data);
        sendEmail(user.getEmail(), announcementTitle, announcementBody);
        sendPush(null, announcementTitle, announcementBody);
        log.info("Announcement sent to user: {}", userId);
    }

    public void broadcastAnnouncement(Iterable<UUID> userIds, String announcementTitle, String announcementBody) {
        userIds.forEach(uid -> sendAnnouncement(uid, announcementTitle, announcementBody));
    }

    // ─── Read management ─────────────────────────────────────────────────────

    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Notification not found: " + notificationId));
        notification.setIsRead(true);
        log.info("Notification marked as read: {}", notificationId);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
        log.info("All notifications marked as read for user: {}", userId);
    }
}