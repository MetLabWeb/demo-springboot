package com.met.metcamp.web.demospringboot.repository;

import com.met.metcamp.web.demospringboot.entities.model.Event;
import com.met.metcamp.web.demospringboot.exceptions.RepoException;
import com.met.metcamp.web.demospringboot.utils.MapperUtils;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public class EventRepository {

    private static final Logger logger = LogManager.getLogger();

    @Getter
    private final ArrayList<Event> events;

    private final MapperUtils mapperUtils;

    public EventRepository(MapperUtils mapperUtils) {
        this.mapperUtils = mapperUtils;
        this.events = loadEvents();
    }

    private ArrayList<Event> loadEvents() {
        String path = "src/main/resources/repository/events.json";
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            String input = new String(bytes);
            return this.mapperUtils.mapToEventList(input);
        } catch (IOException io) {
            logger.fatal("Error reading file located at {} ", path);
            throw new RepoException("Error reading file");
        }
    }

    private void save() {
        try {
            String datos = mapperUtils.mapToJson(events);
            Files.writeString(Paths.get("src/main/resources/repository/events.json"), datos);
        } catch (IOException io) {
            throw new RepoException("Error writing file");
        }
    }

    public Optional<Event> find(int id) {
        return events.stream().filter(e -> e.getId() == id).findFirst();
    }

    public void add(Event newEvent) {
        events.add(newEvent);
        save();
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
