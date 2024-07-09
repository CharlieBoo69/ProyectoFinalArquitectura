package com.example.MicroGestionEventos.service;

import com.example.MicroGestionEventos.model.Event;
import com.example.MicroGestionEventos.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String notificationServiceUrl = "http://localhost:8083/api/notifications"; // URL del MicroNotificaciones
    private final String participantServiceUrl = "http://localhost:8082/api/participants"; // URL del MicroGestionParticipantes

    @Cacheable(value = "events", key = "#idEvent")
    public Event getEventByIdEvent(String idEvent) {
        return eventRepository.findByIdEvent(idEvent);
    }

    @Cacheable(value = "events")
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @CacheEvict(value = "events", allEntries = true)
    public Event createEvent(Event event) {
        event.setCreationTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

    @CacheEvict(value = "events", key = "#idEvent")
    public Event updateEvent(String idEvent, Event event) {
        Event existingEvent = eventRepository.findByIdEvent(idEvent);
        if (existingEvent != null) {
            event.setId(existingEvent.getId());
            event.setCreationTime(existingEvent.getCreationTime());
            return eventRepository.save(event);
        }
        return null;
    }

    @CacheEvict(value = "events", key = "#idEvent")
    public void deleteEvent(String idEvent) {
        eventRepository.deleteByIdEvent(idEvent);
    }

    @CacheEvict(value = "events", key = "#idEvent")
    public void addGuestToEvent(String idEvent, String participantId, String userId) throws Exception {
        Event event = eventRepository.findByIdEvent(idEvent);
        if (event != null) {
            if (event.getType().equalsIgnoreCase("private")) {
                // Permitir solo al dueño agregar participantes
                if (!event.getIdOwner().equals(userId)) {
                    throw new Exception("Only the owner can add participants to a private event.");
                }
            }
            if (!event.getGuests().contains(participantId)){
                event.getGuests().add(participantId);
                eventRepository.save(event);

                // Actualizar la lista de eventos del participante
                restTemplate.put(participantServiceUrl + "/" + participantId + "/events/" + idEvent, null);

                // Enviar notificaciones pendientes al nuevo invitado
                for (String notificationId : event.getNotifications()) {
                    restTemplate.put(notificationServiceUrl + "/" + notificationId + "/participants/" + participantId, null);
                }

                // Invalidar la caché después de actualizar el evento
                eventCacheEvict(idEvent);
            }
        }
    }

    @CacheEvict(value = "events", key = "#idEvent")
    public void eventCacheEvict(String idEvent) {
        // Este método se usa solo para invalidar la caché
    }

    public void removeGuestFromEvent(String idEvent, String participantId) {
        Event event = eventRepository.findByIdEvent(idEvent);
        if (event != null) {
            event.getGuests().remove(participantId);
            eventRepository.save(event);

            // Actualizar la lista de eventos del participante
            restTemplate.delete(participantServiceUrl + "/" + participantId + "/events/" + idEvent);
        }
    }


    public void addNotificationToEvent(String idEvent, String notificationId) {
        Event event = eventRepository.findByIdEvent(idEvent);
        if (event != null) {
            event.getNotifications().add(notificationId);
            eventRepository.save(event);
        }
    }
}