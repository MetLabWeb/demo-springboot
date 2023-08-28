package com.met.metcamp.web.demospringboot.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/met/metcamp/web/events")
public class EventController {

    @GetMapping
    public ResponseEntity getAllEvents() {
        return ResponseEntity.ok(Map.of("events:", "[GET Lista de eventos]"));
    }

    @GetMapping("/{id}")
    public ResponseEntity getEventById(@PathVariable int id) {
        //hacer cosas
        return ResponseEntity.ok(Map.of("events:", String.format("[GET evento con id %s]", id)));
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody String body) {
        //hacemos cosas....
        if (body.contains("*")) {
            return ResponseEntity.badRequest().body("LNo se permiten caracteres especiales");
        } else {
            return ResponseEntity.ok(Map.of("datos recibidos:", body));
        }

    }

}
