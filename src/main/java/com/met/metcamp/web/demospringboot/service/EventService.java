package com.met.metcamp.web.demospringboot.service;

import com.met.metcamp.web.demospringboot.entities.model.Event;
import com.met.metcamp.web.demospringboot.exceptions.ApiException;
import com.met.metcamp.web.demospringboot.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Event createEvent(Event event) {
        checkDates(event);
        repository.add(event);
        return event;
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
                            checkDates(newData);
                            event.update(newData);
                            repository.update(id, event);
                        },
                        () -> {
                            throw new ApiException(404,
                                    String.format("Event %s doesn't exists", id));
                        }
                );
    }

    public void deleteEvent(int id) {
        repository.find(id)
                .ifPresentOrElse(event -> repository.delete(id),
                        () -> {
                            throw new ApiException(404,
                                    String.format("Event %s doesn't exists", id));
                        }
                );
    }

    private void checkDates(Event event) {
        if (event.getStartDateTime().isAfter(event.getEndDateTime())) {
            throw new ApiException(400, "Start date must be before end date");
        }
    }
}
