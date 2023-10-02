package com.met.metcamp.web.demospringboot.repository;

import com.met.metcamp.web.demospringboot.entities.model.Event;
import com.met.metcamp.web.demospringboot.exceptions.RepoException;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

import static com.met.metcamp.web.demospringboot.utils.MapperUtils.mapToEventList;
import static com.met.metcamp.web.demospringboot.utils.MapperUtils.mapToJson;

@Repository
public class EventRepository {

    private static final Logger logger = LogManager.getLogger();
    private static final String PATH = "classpath:repository/events.json";

    @Getter
    private final ArrayList<Event> events;

    private Integer nextId;

    public EventRepository() {
        this.events = loadEvents();
        this.nextId = events.stream().mapToInt(Event::getId).max().orElse(1);
    }

    private ArrayList<Event> loadEvents() {
        try {
            byte[] bytes = Files.readAllBytes(Path.of(PATH));
            String input = new String(bytes);
            return mapToEventList(input);
        } catch (IOException io) {
            logger.fatal("Error reading file located at {} ", PATH);
            throw new RepoException("Error reading file");
        }
    }

    private void save() {
        try {
            String datos = mapToJson(events);
            Files.writeString(Path.of(PATH), datos);
        } catch (IOException io) {
            throw new RepoException("Error writing file");
        }
    }

    public Optional<Event> find(int id) {
        return events.stream().filter(e -> e.getId() == id).findFirst();
    }

    public void add(Event newEvent) {
        newEvent.setId(nextId);
        events.add(newEvent);
        save();
        nextId += 1;
    }

    public void delete(int id) {
        events.removeIf(e -> e.getId() == id);
        save();
    }

    public void update(int id, Event newEventData) {
        events.removeIf(e -> e.getId() == id);
        events.add(newEventData);
        save();
    }
}
