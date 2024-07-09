package com.example.MicroNotificaciones.controller;

import com.example.MicroNotificaciones.model.Notification;
import com.example.MicroNotificaciones.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/{notificationId}")
    public Notification getNotificationByNotificationId(@PathVariable String notificationId) {
        return notificationService.getNotificationByNotificationId(notificationId);
    }

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }

    @PutMapping("/{notificationId}")
    public Notification updateNotification(@PathVariable String notificationId, @RequestBody Notification notification) {
        return notificationService.updateNotification(notificationId, notification);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
    }

    @PutMapping("/{notificationId}/participants/{participantId}")
    public void addParticipantToNotification(@PathVariable String notificationId, @PathVariable String participantId) {
        notificationService.addParticipantToNotification(notificationId, participantId);
    }
}