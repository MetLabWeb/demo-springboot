package com.met.metcamp.web.demospringboot.service;

import com.met.metcamp.web.demospringboot.entities.model.Event;
import com.met.metcamp.web.demospringboot.exceptions.ApiException;
import com.met.metcamp.web.demospringboot.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository repository;
    private final ValidationService validationService;

    public EventService(EventRepository repository, ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    public Event createEvent(Event event) {
        validationService.validateCreateEvent(event);
        Optional<Event> foundEvent = repository.find(event.getId());
        if (foundEvent.isPresent()) {
            throw new ApiException(400, String.format("Event %s already exists", event.getId()));
        } else {
            repository.add(event);
            return event;
        }
    }

    public List<Event> getAllEvents() {
        return repository.getEvents();
    }

    public Event getEventById(int id) {
        return repository.find(id).orElseThrow(() -> new ApiException(404,
                                String.format("Event %s doesn't exists", id))
                        );
    }

    public void updateEvent(int id, Event newData) {
        repository.find(id)
                .ifPresentOrElse(event -> {
                            event.update(newData);
                            validationService.validateUpdateEvent(event);
                            repository.update(id, event);
                        },
                        () -> { throw new ApiException(404,
                                String.format("Event %s doesn't exists", id)); }
                );
    }

    public void deleteEvent(int id) {
        repository.find(id)
                .ifPresentOrElse(event -> repository.delete(id),
                                 () -> { throw new ApiException(404,
                                         String.format("Event %s doesn't exists", id)); }
                );
    }

}
