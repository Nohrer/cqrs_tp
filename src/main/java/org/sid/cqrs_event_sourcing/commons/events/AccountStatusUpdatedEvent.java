package org.sid.cqrs_event_sourcing.commons.events;


import org.sid.cqrs_event_sourcing.commons.enums.AccountStatus;

public record AccountStatusUpdatedEvent(String accountId, AccountStatus fromStatus, AccountStatus toStatus) {
}
