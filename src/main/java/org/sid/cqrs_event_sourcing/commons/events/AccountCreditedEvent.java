package org.sid.cqrs_event_sourcing.commons.events;

public record AccountCreditedEvent(String accountId, double amount) {
}
