package com.met.metcamp.web.demospringboot.service;

import com.met.metcamp.web.demospringboot.entities.model.Event;
import com.met.metcamp.web.demospringboot.exceptions.ApiException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ValidationService {

    public void validateCreateEvent(Event event) {
        validateId(event.getId());
        validateName(event.getName());
        validateDates(event.getStartDateTime(), event.getEndDateTime());
    }
    public void validateUpdateEvent(Event event) {
        validateName(event.getName());
        validateDates(event.getStartDateTime(), event.getEndDateTime());
    }

    public void validateName (String name) {
        if (name == null || name.isEmpty()) {
            throw new ApiException(400, "name is required");
        }
        if (name.length() < 5) {
            throw new ApiException(400, "name is too short");
        }
    }

    public void validateId(int id) {
        if(id == 0) {
            throw new ApiException(400, "id must not be zero");
        }  else if (id < 0) {
            throw new ApiException(400, "id must be positive");
        }
    }

    public void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ApiException(400, "start date must be before end date");
        }
    }
}
