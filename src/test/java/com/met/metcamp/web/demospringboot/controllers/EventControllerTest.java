package com.met.metcamp.web.demospringboot.controllers;

import com.met.metcamp.web.demospringboot.entities.model.Event;
import com.met.metcamp.web.demospringboot.exceptions.ApiException;
import com.met.metcamp.web.demospringboot.exceptions.ConvertionException;
import com.met.metcamp.web.demospringboot.exceptions.RepoException;
import com.met.metcamp.web.demospringboot.service.EventService;
import com.met.metcamp.web.demospringboot.testutils.EventListProvider;
import com.met.metcamp.web.demospringboot.utils.MapperUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Stream;

import static com.met.metcamp.web.demospringboot.testutils.EventTestUtils.validFreeEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    private static final String BASE_URL = "/met/metcamp/web/events";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MapperUtils utils = new MapperUtils();

    @MockBean
    private EventService eventService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "{index} GET all")
    @ArgumentsSource(EventListProvider.class)
    void testGetAllEvents(ArrayList<Event> eventList) throws Exception {
        when(eventService.getAllEvents()).thenReturn(eventList);

        MockHttpServletRequestBuilder request = get(BASE_URL);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(content().json(String.format("{\"events\":%s}", utils.mapToJson(eventList))));
    }

    @Test
    @DisplayName("testing GET /id")
    void testGetById() throws Exception {
        when(eventService.getEventById(1)).thenReturn(validFreeEvent)
                                          .thenThrow(new ApiException(404, "Event 1 doesn't exists"));

        MockHttpServletRequestBuilder request = get(String.format("%s/%s", BASE_URL, 1));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{\"event\":%s}", utils.mapToJson(validFreeEvent))));

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Event 1 doesn't exists\"}"));
    }

    @DisplayName("testing POST BadRequest")
    @ParameterizedTest(name = "{index} {1}")
    @MethodSource("inputForPostEventsBadRequest")
    void testPostEventsBadRequest(String body, String expectedMessage) throws Exception {
        when(eventService.createEvent(any(Event.class))).thenThrow(new ApiException(400, "Start date must be before end date"));

        MockHttpServletRequestBuilder request = post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().json(String.format("{\"message\":\"%s\"}", expectedMessage)));
    }

    @DisplayName("testing POST Ok")
    @ParameterizedTest(name = "{index} POST Ok")
    @MethodSource("inputForPostEventsOk")
    void testPostEventsOk(String body) throws Exception {
        Event inputEvent = utils.mapToEvent(body);
        Event expectedEvent = utils.mapToEvent(body);
        expectedEvent.setId(1);

        when(eventService.createEvent(any(Event.class))).thenReturn(expectedEvent);

        MockHttpServletRequestBuilder request = post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(String.format("{\"event\":%s}", utils.mapToJson(expectedEvent))));

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).createEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).usingRecursiveComparison().isEqualTo(inputEvent);
    }

    @Test
    @DisplayName("testing PUT /id not found")
    void testUpdateNotFound() throws Exception {
        doThrow(new ApiException(404, "Event 1 doesn't exists"))
                .when(eventService).updateEvent(eq(1), any(Event.class));

        MockHttpServletRequestBuilder request = put(String.format("%s/%s", BASE_URL, 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(utils.mapToJson(validFreeEvent));

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Event 1 doesn't exists\"}"));
    }

    @DisplayName("testing PUT /id BadRequest")
    @ParameterizedTest(name = "{index} {1}")
    @MethodSource("inputForPostEventsBadRequest")
    void testUpdateBadRequest(String body, String expectedMessage) throws Exception {
        doThrow(new ApiException(400, "Start date must be before end date"))
                .when(eventService).updateEvent(eq(1), any(Event.class));

        MockHttpServletRequestBuilder request = put(String.format("%s/%s", BASE_URL, 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().json(String.format("{\"message\":\"%s\"}", expectedMessage)));
    }

    @DisplayName("testing PUT /id ok")
    @ParameterizedTest(name = "{index} POST Ok")
    @MethodSource("inputForPostEventsOk")
    void testUpdateOk(String body) throws Exception {
        Event inputEvent = utils.mapToEvent(body);

        MockHttpServletRequestBuilder request = put(String.format("%s/%s", BASE_URL, 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isNullOrEmpty();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).updateEvent(eq(1), eventCaptor.capture());
        assertThat(eventCaptor.getValue()).usingRecursiveComparison().isEqualTo(inputEvent);
    }

    @Test
    @DisplayName("testing DELETE /id not found")
    void testDeleteNotFound() throws Exception {
        doThrow(new ApiException(404, "Event 1 doesn't exists"))
                .when(eventService).deleteEvent(1);

        MockHttpServletRequestBuilder request = delete(String.format("%s/%s", BASE_URL, 1));

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Event 1 doesn't exists\"}"));
    }

    @Test
    @DisplayName("testing DELETE /id ok")
    void testDeleteOk() throws Exception {
        MockHttpServletRequestBuilder request = delete(String.format("%s/%s", BASE_URL, 1));

        MvcResult result = mockMvc.perform(request)
                        .andExpect(status().isNoContent())
                        .andReturn();

        assertThat(result.getResponse().getContentAsString()).isNullOrEmpty();
    }

    @DisplayName("testing Error Handling")
    @ParameterizedTest(name = "{index} exception {0}")
    @MethodSource("exceptionHandlingInput")
    void testErrorHandler(RuntimeException e, String message) throws Exception {
        when(eventService.getEventById(1)).thenThrow(e);

        MockHttpServletRequestBuilder request = get(String.format("%s/%s", BASE_URL, 1));

        mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(String.format("{\"message\":\"%s\"}", message)));
    }

    static Stream<Arguments> inputForPostEventsBadRequest() {
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(2);
        String formattedStart = FORMATTER.format(startDate);
        String formattedEnd = FORMATTER.format(endDate);
        String validFreeEvent = "{\"type\":\"ANIVERSARIO\",\"name\":\"Gala MeT\",\"attendees\":200,\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\"}";
        String validBaseEvent = "{\"type\":\"ANIVERSARIO\",\"name\":\"Gala MeT\",\"attendees\":200,\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\",\"prices\":[%s]}";
        return java.util.stream.Stream.of(
                //dates
                Arguments.of(String.format(validFreeEvent, FORMATTER.format(startDate.plusDays(1)), formattedEnd), "Start date must be before end date"),
                Arguments.of(String.format(validFreeEvent, FORMATTER.format(startDate.minusDays(1)), formattedEnd), "start date must be in the future"),
                Arguments.of(String.format(validFreeEvent, StringUtils.EMPTY, formattedEnd), "start date is required"),
                Arguments.of(String.format(validFreeEvent, formattedStart, StringUtils.EMPTY), "end date is required"),
                //event type
                Arguments.of(String.format("{\"name\":\"Gala MeT\",\"attendees\":200,\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "Type is required and must be a valid value"),
                Arguments.of(String.format("{\"type\":\"something\",\"name\":\"Gala MeT\",\"attendees\":200,\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "Type is required and must be a valid value"),
                Arguments.of(String.format("{\"type\":\"\",\"name\":\"Gala MeT\",\"attendees\":200,\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "Type is required and must be a valid value"),
                //name
                Arguments.of(String.format("{\"type\":\"CLASE_METCAMP\",\"attendees\":200,\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "name is required"),
                Arguments.of(String.format("{\"type\":\"CLASE_METCAMP\",\"name\":\"\",\"attendees\":200,\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "name is required"),
                //attendees
                Arguments.of(String.format("{\"type\":\"ENCUENTRO_METLAB\",\"name\":\"Gala MeT\",\"attendees\":0,\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "attendees must be greater than 0"),
                Arguments.of(String.format("{\"type\":\"ENCUENTRO_METLAB\",\"name\":\"Gala MeT\",\"attendees\":-10,\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "attendees must be greater than 0"),
                Arguments.of(String.format("{\"type\":\"ENCUENTRO_METLAB\",\"name\":\"Gala MeT\",\"organizer\":\"MeT\",\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "attendees is required"),
                //organizer
                Arguments.of(String.format("{\"type\":\"ANIVERSARIO\",\"name\":\"Gala MeT\",\"attendees\":200,\"organizer\":\"\",\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "organizer is required"),
                Arguments.of(String.format("{\"type\":\"ANIVERSARIO\",\"name\":\"Gala MeT\",\"attendees\":200,\"start_date\":\"%s\",\"end_date\":\"%s\"}", formattedStart, formattedEnd), "organizer is required"),

                //TicketType
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"currency\":\"ARS\",\"value\":2500.0}"), "ticket type is required and must be a valid value"),
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"other\",\"currency\":\"ARS\",\"value\":2500.0}"), "ticket type is required and must be a valid value"),
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"\",\"currency\":\"ARS\",\"value\":2500.0}"), "ticket type is required and must be a valid value"),
                //currency
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"REGULAR_FULL_PASS\",\"value\":2500.0}"), "currency is required and must be a valid value"),
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"REGULAR_FULL_PASS\",\"currency\":\"\",\"value\":2500.0}"), "currency is required and must be a valid value"),
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"REGULAR_ONE_DAY\",\"currency\":\"other\",\"value\":2500.0}"), "currency is required and must be a valid value"),
                //value
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"REGULAR_ONE_DAY\",\"currency\":\"ARS\"}"), "value is required"),
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"VIP_FULL_PASS\",\"currency\":\"ARS\",\"value\":0}"), "value must be greater than 0.00"),
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"VIP_FULL_PASS\",\"currency\":\"COP\",\"value\":0.0}"), "value must be greater than 0.00"),
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"VIP_ONE_DAY\",\"currency\":\"CLP\",\"value\":-10.0}"), "value must be greater than 0.00"),
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"VIP_ONE_DAY\",\"currency\":\"USD\",\"value\":-2}"), "value must be greater than 0.00"),
                //duplicate price
                Arguments.of(String.format(validBaseEvent, formattedStart, formattedEnd, "{\"type\":\"VIP_ONE_DAY\",\"currency\":\"USD\",\"value\":2},{\"type\":\"VIP_ONE_DAY\",\"currency\":\"USD\",\"value\":5}"), "multiple prices for same ticket type are not allowed")

        );
    }

    static Stream<Arguments> inputForPostEventsOk() {
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(2);
        String formattedStart = FORMATTER.format(startDate);
        String formattedEnd = FORMATTER.format(endDate);
        String freeEvent = "{\"type\":\"%s\",\"name\":\"%s\",\"attendees\":%s,\"organizer\":\"%s\",\"start_date\":\"%s\",\"end_date\":\"%s\"}";
        String pricedEvent = "{\"type\":\"%s\",\"name\":\"%s\",\"attendees\":%s,\"organizer\":\"%s\",\"start_date\":\"%s\",\"end_date\":\"%s\",\"prices\":[%s]}";

        return java.util.stream.Stream.of(
                Arguments.of(String.format(freeEvent, "ENCUENTRO_METLAB", "MetLab Web", 200, "MeT", formattedStart, formattedEnd)),
                Arguments.of(String.format(pricedEvent, "CLASE_METCAMP", "Gala Met", 200, "MeT", formattedStart, formattedEnd,
                        "{\"type\":\"REGULAR_FULL_PASS\",\"currency\":\"ARS\",\"value\":9000.50}")),
                Arguments.of(String.format(pricedEvent, "ANIVERSARIO", "Gala Met", 200, "MeT", formattedStart, formattedEnd,
                        "{\"type\":\"REGULAR_ONE_DAY\",\"currency\":\"ARS\",\"value\":7000},{\"type\":\"VIP_ONE_DAY\",\"currency\":\"ARS\",\"value\":3500.0}"))
        );
    }

    static Stream<Arguments> exceptionHandlingInput() {
        return Stream.of(
                Arguments.of(new ConvertionException(new RuntimeException()), "Internal error"),
                Arguments.of(new RepoException("ERROR"), "Internal error"),
                Arguments.of(new RuntimeException(), "Generic error")
        );
    }
}
