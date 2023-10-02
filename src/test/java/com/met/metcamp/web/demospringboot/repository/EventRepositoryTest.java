package com.met.metcamp.web.demospringboot.repository;

import com.met.metcamp.web.demospringboot.entities.model.Event;
import com.met.metcamp.web.demospringboot.exceptions.ConvertionException;
import com.met.metcamp.web.demospringboot.exceptions.RepoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.nio.file.Path;
import java.time.temporal.ChronoUnit;

import static com.met.metcamp.web.demospringboot.testutils.EventTestUtils.eventToCreate;
import static com.met.metcamp.web.demospringboot.testutils.EventTestUtils.eventToUpdate;
import static com.met.metcamp.web.demospringboot.testutils.EventTestUtils.validPricedEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class EventRepositoryTest {
    private static final Path PATH_OK = Path.of("src/test/resources/repository/events.json");
    private static final Path PATH_MALFORMED = Path.of("src/test/resources/repository/malformed.json");
    private static final Path PATH_NON_EXISTING = Path.of("");

    @Test
    @DisplayName("testing full repository happy path")
    void testFullClassOk() {
        //load empty
        try (MockedStatic<Path> pathMocked = mockStatic(Path.class)) {
            pathMocked.when((MockedStatic.Verification) Path.of(anyString())).thenReturn(PATH_OK);
            EventRepository repository = assertDoesNotThrow(EventRepository::new);
            assertThat(repository.getEvents()).isEmpty();
        }

        //add event + get all + find by id
        Event newEvent = eventToCreate();

        try (MockedStatic<Path> pathMocked = mockStatic(Path.class)) {
            pathMocked.when((MockedStatic.Verification) Path.of(anyString())).thenReturn(PATH_OK);
            EventRepository repository = new EventRepository();

            repository.add(newEvent);

            assertThat(repository.getEvents())
                    .isNotEmpty()
                    .satisfiesExactly(e -> {
                        assertEquals(1, e.getId());
                        assertEquals(newEvent.getName(), e.getName());
                        assertEquals(newEvent.getType(), e.getType());
                        assertEquals(newEvent.getOrganizer(), e.getOrganizer());
                        assertEquals(newEvent.getAttendees(), e.getAttendees());
                        assertEquals(newEvent.getStartDateTime().truncatedTo(ChronoUnit.SECONDS), e.getStartDateTime().truncatedTo(ChronoUnit.SECONDS));
                        assertEquals(newEvent.getEndDateTime().truncatedTo(ChronoUnit.SECONDS), e.getEndDateTime().truncatedTo(ChronoUnit.SECONDS));
                        assertNull(e.getPrices());
                    });

            assertThat(repository.find(1)).isPresent();
        }

        //update event
        Event updateData = eventToUpdate();
        updateData.setId(1);

        try (MockedStatic<Path> pathMocked = mockStatic(Path.class)) {
            pathMocked.when((MockedStatic.Verification) Path.of(anyString())).thenReturn(PATH_OK);
            EventRepository repository = new EventRepository();

            repository.update(1, updateData);

            assertThat(repository.getEvents())
                    .isNotEmpty()
                    .satisfiesExactly(e -> {
                        assertEquals(1, e.getId());
                        assertEquals(updateData.getName(), e.getName());
                        assertEquals(updateData.getType(), e.getType());
                        assertEquals(updateData.getOrganizer(), e.getOrganizer());
                        assertEquals(updateData.getAttendees(), e.getAttendees());
                        assertEquals(updateData.getStartDateTime().truncatedTo(ChronoUnit.SECONDS), e.getStartDateTime().truncatedTo(ChronoUnit.SECONDS));
                        assertEquals(updateData.getEndDateTime().truncatedTo(ChronoUnit.SECONDS), e.getEndDateTime().truncatedTo(ChronoUnit.SECONDS));
                        assertEquals(1, e.getPrices().size());
                        assertEquals(updateData.getPrices().get(0).getType(), e.getPrices().get(0).getType());
                        assertEquals(updateData.getPrices().get(0).getCurrency(), e.getPrices().get(0).getCurrency());
                        assertEquals(updateData.getPrices().get(0).getValue(), e.getPrices().get(0).getValue());
                    });
        }

        //delete event
        try (MockedStatic<Path> pathMocked = mockStatic(Path.class)) {
            pathMocked.when((MockedStatic.Verification) Path.of(anyString())).thenReturn(PATH_OK);
            EventRepository repository = new EventRepository();

            repository.delete(1);

            assertThat(repository.getEvents()).isEmpty();
        }
    }

    @Test
    @DisplayName("testing non existing file")
    void testNonExistingFile() {
        try (MockedStatic<Path> pathMocked = mockStatic(Path.class)) {
            pathMocked.when((MockedStatic.Verification) Path.of(anyString()))
                    .thenReturn(PATH_NON_EXISTING)
                    .thenReturn(PATH_OK)
                    .thenReturn(PATH_NON_EXISTING);
            RepoException readingException = assertThrows(RepoException.class, () -> new EventRepository());
            assertEquals("Error reading file", readingException.getMessage());

            EventRepository repository = assertDoesNotThrow(EventRepository::new);

            RepoException savingException = assertThrows(RepoException.class, () -> repository.add(validPricedEvent));
            assertEquals("Error writing file", savingException.getMessage());
        }
    }

    @Test
    @DisplayName("testing malformed file")
    void testMalformedFile() {
        try (MockedStatic<Path> pathMocked = mockStatic(Path.class)) {
            pathMocked.when((MockedStatic.Verification) Path.of(anyString()))
                    .thenReturn(PATH_MALFORMED);
            assertThrows(ConvertionException.class, () -> new EventRepository());
        }
    }
}
