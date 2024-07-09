package com.example.MicroGestionParticipantes.repository;

import com.example.MicroGestionParticipantes.model.Participant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends MongoRepository<Participant, String>{
    Participant findByParticipantId(String participantId);
    void deleteByParticipantId(String participantId);
}
