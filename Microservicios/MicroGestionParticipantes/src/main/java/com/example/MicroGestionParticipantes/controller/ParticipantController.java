package com.example.MicroGestionParticipantes.controller;

import com.example.MicroGestionParticipantes.model.Participant;
import com.example.MicroGestionParticipantes.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {
    @Autowired
    private ParticipantService participantService;

    @GetMapping
    public List<Participant> getAllParticipants() {
        return participantService.getAllParticipants();
    }

    @GetMapping("/{participantId}")
    public Participant getParticipantByParticipantId(@PathVariable String participantId) {
        return participantService.getParticipantByParticipantId(participantId);
    }

    @PostMapping
    public Participant createParticipant(@RequestBody Participant participant) {
        return participantService.createParticipant(participant);
    }

    @PutMapping("/{participantId}")
    public Participant updateParticipant(@PathVariable String participantId, @RequestBody Participant participant) {
        return participantService.updateParticipant(participantId, participant);
    }

    @DeleteMapping("/{participantId}")
    public void deleteParticipant(@PathVariable String participantId) {
        participantService.deleteParticipant(participantId);
    }

    @PutMapping("/{participantId}/events/{eventId}")
    public Participant addEventToParticipant(@PathVariable String participantId, @PathVariable String eventId) throws Exception{
        participantService.addEventToParticipant(participantId, eventId);
        return participantService.getParticipantByParticipantId(participantId);
    }

    @DeleteMapping("/{participantId}/events/{eventId}")
    public void removeEventFromParticipant(@PathVariable String participantId, @PathVariable String eventId) {
        participantService.removeEventFromParticipant(participantId, eventId);
    }

    @PutMapping("/{participantId}/notifications/{notificationId}")
    public void addNotificationToParticipant(@PathVariable String participantId, @PathVariable String notificationId) {
        participantService.addNotificationToParticipant(participantId, notificationId);
    }
}