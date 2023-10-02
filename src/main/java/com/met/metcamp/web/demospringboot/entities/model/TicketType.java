package com.met.metcamp.web.demospringboot.entities.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.met.metcamp.web.demospringboot.exceptions.ApiException;

public enum TicketType {
    REGULAR_FULL_PASS,
    REGULAR_ONE_DAY,
    VIP_FULL_PASS,
    VIP_ONE_DAY;
}
