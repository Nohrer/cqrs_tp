package org.sid.cqrs_event_sourcing.commons.dtos;

import lombok.*;
import org.sid.cqrs_event_sourcing.query.entities.OperationType;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OperationDTO {
    private Long id;
    private Instant date;
    private double amount;
    private OperationType type;
    private String accountId;
}
