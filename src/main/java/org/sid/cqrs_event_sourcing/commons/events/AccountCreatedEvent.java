package org.sid.cqrs_event_sourcing.commons.events;


import org.sid.cqrs_event_sourcing.commons.enums.AccountStatus;

public record AccountCreatedEvent(String accountId, double initialBalance, String currency, AccountStatus accountStatus) {
}
