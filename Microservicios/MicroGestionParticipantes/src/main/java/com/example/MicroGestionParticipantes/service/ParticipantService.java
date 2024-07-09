package com.example.MicroGestionParticipantes.service;

import com.example.MicroGestionParticipantes.model.Notification;
import com.example.MicroGestionParticipantes.model.Participant;
import com.example.MicroGestionParticipantes.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParticipantService {
    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String eventServiceUrl = "http://localhost:8081/api/events";

    @Cacheable(value = "participants", key = "#participantId")
    public Participant getParticipantByParticipantId(String participantId) {
        return participantRepository.findByParticipantId(participantId);
    }

    @Cacheable(value = "participants")
    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    @CacheEvict(value = "participants", allEntries = true)
    public Participant createParticipant(Participant participant) {
        participant.setRegistrationDate(LocalDateTime.now());
        Participant savedParticipant = participantRepository.save(participant);

        // Asignar notificaciones pendientes al nuevo participante
        assignPendingNotifications(savedParticipant);

        return savedParticipant;
    }

    private void assignPendingNotifications(Participant participant) {
        Query query = new Query();
        query.addCriteria(Criteria.where("guestList").in(participant.getParticipantId()));
        List<Notification> pendingNotifications = mongoTemplate.find(query, Notification.class, "pending_notifications");

        for (Notification notification : pendingNotifications) {
            addNotificationToParticipant(participant.getParticipantId(), notification.getNotificationId());
        }
    }

    @CacheEvict(value = "participants", key = "#participantId")
    public Participant updateParticipant(String participantId, Participant participant) {
        Participant existingParticipant = participantRepository.findByParticipantId(participantId);
        if (existingParticipant != null) {
            participant.setId(existingParticipant.getId());
            participant.setRegistrationDate(existingParticipant.getRegistrationDate());
            return participantRepository.save(participant);
        }
        return null;
    }

    @CacheEvict(value = "participants", key = "#participantId")
    public void deleteParticipant(String participantId) {
        participantRepository.deleteByParticipantId(participantId);
    }

    public void addEventToParticipant(String participantId, String idEvent) throws Exception {
        Participant participant = participantRepository.findByParticipantId(participantId);
        if (participant != null) {
            if (!participant.getEvents().contains(idEvent)) {
                participant.getEvents().add(idEvent);
                participantRepository.save(participant);

                // Actualiza la lista de invitados del evento
                restTemplate.put(eventServiceUrl + "/" + idEvent + "/guests/" + participantId + "?userId=" + participantId, null);

                // Asignar notificaciones pendientes al participante para este evento
                assignPendingNotifications(participant);
            }
        } else {
            throw new Exception("Participant not found");
        }
    }

    public void removeEventFromParticipant(String participantId, String eventId) {
        Participant participant = participantRepository.findByParticipantId(participantId);
        if (participant != null) {
            if (participant.getEvents().contains(eventId)) {
                participant.getEvents().remove(eventId);
                participantRepository.save(participant);

                // Actualizar la lista de invitados del evento
                restTemplate.delete(eventServiceUrl + "/" + eventId + "/guests/" + participantId + "?userId=" + participantId);
            }
        }
    }

    // Nuevo método para agregar una notificación a un participante
    public void addNotificationToParticipant(String participantId, String notificationId) {
        Participant participant = participantRepository.findByParticipantId(participantId);
        if (participant != null) {
            if (!participant.getNotifications().contains(notificationId)) {
                participant.getNotifications().add(notificationId);
                participantRepository.save(participant);
            }
        }
    }
}