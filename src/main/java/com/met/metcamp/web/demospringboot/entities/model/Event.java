package com.met.metcamp.web.demospringboot.entities.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.met.metcamp.web.demospringboot.exceptions.ConvertionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {

    private int id;
    private EventType type;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty(value = "start_date")
    private LocalDateTime startDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty(value = "end_date")
    private LocalDateTime endDateTime;

    private Integer attendees;
    private String organizer;
    private List<Price> prices;

    public void update(Event newEventData) {
        this.type = newEventData.getType() != null ? newEventData.getType() : this.type;
        this.name = newEventData.getName() != null ? newEventData.getName() : this.name;
        this.startDateTime = newEventData.getStartDateTime() != null ? newEventData.getStartDateTime() : this.startDateTime;
        this.endDateTime = newEventData.getEndDateTime() != null ? newEventData.getEndDateTime() : this.endDateTime;
        this.attendees = newEventData.getAttendees() != null ? newEventData.getAttendees() : this.attendees;
        this.organizer = newEventData.getOrganizer() != null ? newEventData.getOrganizer() : this.organizer;
        this.prices = newEventData.getPrices() != null ? newEventData.getPrices() : this.prices;
    }
}
