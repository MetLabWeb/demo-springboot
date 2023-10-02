package com.met.metcamp.web.demospringboot.controllers;

import com.met.metcamp.web.demospringboot.entities.model.Event;
import com.met.metcamp.web.demospringboot.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/met/metcamp/web/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        return ResponseEntity.ok(Map.of("events", eventService.getAllEvents()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Event>> getEventById(@PathVariable int id) {
        return ResponseEntity.ok(Map.of("event", eventService.getEventById(id)));
    }

    @PostMapping
    public ResponseEntity<Map<String, Event>> createEvent(@Valid @RequestBody Event event) {
        return ResponseEntity.status(201).body(Map.of("event", eventService.createEvent(event)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Event>> updateEvent(@PathVariable int id,
                                                          @Valid @RequestBody Event body) {
        eventService.updateEvent(id, body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable int id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
