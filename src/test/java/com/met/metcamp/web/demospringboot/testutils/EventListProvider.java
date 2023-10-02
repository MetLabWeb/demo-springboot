package com.met.metcamp.web.demospringboot.testutils;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.met.metcamp.web.demospringboot.testutils.EventTestUtils.validFreeEvent;
import static com.met.metcamp.web.demospringboot.testutils.EventTestUtils.validPricedEvent;


public class EventListProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("empty", new ArrayList<>())),
                Arguments.of(Named.of("1 element", new ArrayList<>(List.of(validFreeEvent)))),
                Arguments.of(Named.of("multiple elements", new ArrayList<>(List.of(validFreeEvent, validPricedEvent))))
        );
    }
}
