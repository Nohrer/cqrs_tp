package org.sid.cqrs_event_sourcing.commons.dtos;

public record CreateAccountDTO(double initialBalance, String currency) {
}
