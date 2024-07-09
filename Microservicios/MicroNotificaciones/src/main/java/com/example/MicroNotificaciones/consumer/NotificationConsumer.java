package com.example.MicroNotificaciones.consumer;

import com.example.MicroNotificaciones.config.RabbitMQConfig;
import com.example.MicroNotificaciones.model.Notification;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NotificationConsumer {
    @Autowired
    private RestTemplate restTemplate;

    private final String participantServiceUrl = "http://localhost:8082/api/participants"; // URL del MicroGestionParticipantes

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleNotification(Notification notification) {
        // Actualizar la lista de notificaciones de cada participante
        for (String participantId : notification.getGuestList()) {
            restTemplate.put(participantServiceUrl + "/" + participantId + "/notifications/" + notification.getNotificationId(), null);
        }
    }
}