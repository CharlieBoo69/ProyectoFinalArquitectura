package com.example.MicroNotificaciones.repository;

import com.example.MicroNotificaciones.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    Notification findByNotificationId(String notificationId);
}
