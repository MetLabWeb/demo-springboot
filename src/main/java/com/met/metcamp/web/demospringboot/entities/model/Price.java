package com.met.metcamp.web.demospringboot.entities.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@Getter @Setter
@ToString
@NoArgsConstructor
public class Price {
    @NotNull(message = "ticket type is required and must be a valid value")
    private TicketType type;

    @NotNull(message = "currency is required and must be a valid value")
    private Currency currency;

    @NotNull(message = "value is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "value must be greater than 0.00")
    private BigDecimal value;

    @Override
    public int hashCode() {
        return Objects.hash(this.getType().name());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Price that)) return false;
        return this.getType().equals(that.getType());
    }
}
