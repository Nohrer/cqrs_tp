package org.sid.cqrs_event_sourcing.commons.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sid.cqrs_event_sourcing.query.entities.Account;
import org.sid.cqrs_event_sourcing.query.entities.Operation;


import java.util.List;
@AllArgsConstructor @Getter
public class AccountStatement {
    private Account account;
    private List<Operation> operations;
}
