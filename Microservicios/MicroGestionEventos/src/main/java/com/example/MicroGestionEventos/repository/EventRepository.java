package com.example.MicroGestionEventos.repository;

import com.example.MicroGestionEventos.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, String>{
    Event findByIdEvent(String idEvent);
    void deleteByIdEvent(String idEvent);
}
