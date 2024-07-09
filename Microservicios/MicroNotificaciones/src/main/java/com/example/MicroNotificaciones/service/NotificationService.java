package com.example.MicroNotificaciones.service;

import com.example.MicroNotificaciones.model.Event;
import com.example.MicroNotificaciones.model.Notification;
import com.example.MicroNotificaciones.repository.NotificationRepository;
import com.example.MicroNotificaciones.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String participantServiceUrl = "http://localhost:8082/api/participants"; // URL del MicroGestionParticipantes
    private final String eventServiceUrl = "http://localhost:8081/api/events"; // URL del MicroGestionEventos

    @Cacheable(value = "notifications", key = "#notificationId")
    public Notification getNotificationByNotificationId(String notificationId) {
        return notificationRepository.findByNotificationId(notificationId);
    }

    @Cacheable(value = "notifications")
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @CacheEvict(value = "notifications", allEntries = true)
    public Notification createNotification(Notification notification) {
        notification.setCreationDate(LocalDateTime.now());

        // Obtener la lista de invitados del evento
        ResponseEntity<Event> response = restTemplate.getForEntity(eventServiceUrl + "/" + notification.getEventId(), Event.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Event event = response.getBody();
            notification.setGuestList(event.getGuests());

            // Actualizar la lista de notificaciones del evento
            restTemplate.put(eventServiceUrl + "/" + notification.getEventId() + "/notifications/" + notification.getNotificationId(), null);

            // Actualizar la lista de notificaciones de los participantes existentes
            for (String participantId : event.getGuests()) {
                restTemplate.put(participantServiceUrl + "/" + participantId + "/notifications/" + notification.getNotificationId(), null);
            }
        }

        Notification savedNotification = notificationRepository.save(notification);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, savedNotification);

        // Almacenar notificaciones pendientes para nuevos participantes
        storePendingNotifications(savedNotification);

        // Invalidar la caché después de crear la notificación
        notificationCacheEvict(notification.getNotificationId());

        return savedNotification;
    }

    @CacheEvict(value = "notifications", key = "#notificationId")
    public void notificationCacheEvict(String notificationId) {
        // Este método se usa solo para invalidar la caché
    }

    private void storePendingNotifications(Notification notification) {
        mongoTemplate.save(notification, "pending_notifications");
    }

    @CacheEvict(value = "notifications", key = "#notificationId")
    public Notification updateNotification(String notificationId, Notification notification) {
        Notification existingNotification = notificationRepository.findByNotificationId(notificationId);
        if (existingNotification != null) {
            notification.setId(existingNotification.getId());
            notification.setCreationDate(existingNotification.getCreationDate());
            Notification updatedNotification = notificationRepository.save(notification);
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, updatedNotification);
            return updatedNotification;
        }
        return null;
    }

    @CacheEvict(value = "notifications", key = "#notificationId")
    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void addParticipantToNotification(String notificationId, String participantId) {
        Notification notification = notificationRepository.findByNotificationId(notificationId);
        if (notification != null) {
            if (!notification.getGuestList().contains(participantId)) {
                notification.getGuestList().add(participantId);
                notificationRepository.save(notification);
                restTemplate.put(participantServiceUrl + "/" + participantId + "/notifications/" + notificationId, null);
            }
        }
    }
}
