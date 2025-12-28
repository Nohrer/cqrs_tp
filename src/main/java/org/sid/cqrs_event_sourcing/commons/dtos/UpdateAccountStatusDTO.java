package org.sid.cqrs_event_sourcing.commons.dtos;


import org.sid.cqrs_event_sourcing.commons.enums.AccountStatus;

public record UpdateAccountStatusDTO(String accountId, AccountStatus accountStatus) {
}
