package org.sid.cqrs_event_sourcing.query.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class WatchEventQuery {
    private String accountId;
}
