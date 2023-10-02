package com.met.metcamp.web.demospringboot.entities.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private Integer id;

    @NotNull(message = "Type is required and must be a valid value")
    private EventType type;

    @NotBlank(message = "name is required")
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty(value = "start_date")
    @NotNull(message = "start date is required")
    @Future(message = "start date must be in the future")
    private LocalDateTime startDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty(value = "end_date")
    @NotNull(message = "end date is required")
    private LocalDateTime endDateTime;

    @Positive(message = "attendees must be greater than 0")
    @NotNull(message = "attendees is required")
    private Integer attendees;

    @NotBlank(message = "organizer is required")
    private String organizer;

    @UniqueElements(message = "multiple prices for same ticket type are not allowed")
    @Valid
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
