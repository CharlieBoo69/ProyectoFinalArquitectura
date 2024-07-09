package com.example.MicroGestionEventos.controller;

import com.example.MicroGestionEventos.model.Event;
import com.example.MicroGestionEventos.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{idEvent}")
    public Event getEventByIdEvent(@PathVariable String idEvent) {
        return eventService.getEventByIdEvent(idEvent);
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @PutMapping("/{idEvent}")
    public Event updateEvent(@PathVariable String idEvent, @RequestBody Event event) {
        return eventService.updateEvent(idEvent, event);
    }

    @DeleteMapping("/{idEvent}")
    public void deleteEvent(@PathVariable String idEvent) {
        eventService.deleteEvent(idEvent);
    }

    @PutMapping("/{idEvent}/guests/{participantId}")
    public Event addGuestToEvent(@PathVariable String idEvent, @PathVariable String participantId, @RequestParam String userId) throws Exception {
        eventService.addGuestToEvent(idEvent, participantId, userId);
        return eventService.getEventByIdEvent(idEvent);
    }

    @DeleteMapping("/{idEvent}/guests/{participantId}")
    public void removeGuestFromEvent(@PathVariable String idEvent, @PathVariable String participantId) {
        eventService.removeGuestFromEvent(idEvent, participantId);
    }

    @PutMapping("/{idEvent}/notifications/{notificationId}")
    public void addNotificationToEvent(@PathVariable String idEvent, @PathVariable String notificationId) {
        eventService.addNotificationToEvent(idEvent, notificationId);
    }
}