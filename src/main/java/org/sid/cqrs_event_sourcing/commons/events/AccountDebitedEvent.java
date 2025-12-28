package org.sid.cqrs_event_sourcing.commons.events;

public record AccountDebitedEvent(String accountId, double amount) {
}
