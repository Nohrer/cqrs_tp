package org.sid.cqrs_event_sourcing.query.handlers;


import lombok.extern.slf4j.Slf4j;

import org.axonframework.queryhandling.QueryHandler;
import org.sid.cqrs_event_sourcing.commons.dtos.AccountStatement;
import org.sid.cqrs_event_sourcing.query.dtos.AccountEvent;
import org.sid.cqrs_event_sourcing.query.entities.Account;
import org.sid.cqrs_event_sourcing.query.entities.Operation;
import org.sid.cqrs_event_sourcing.query.queries.GetAccountStatement;
import org.sid.cqrs_event_sourcing.query.queries.GetAllAccounts;
import org.sid.cqrs_event_sourcing.query.queries.WatchEventQuery;
import org.sid.cqrs_event_sourcing.query.repository.AccountRepository;
import org.sid.cqrs_event_sourcing.query.repository.OperationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccountQueryHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    public AccountQueryHandler(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }
    @QueryHandler
    public List<Account> on(GetAllAccounts query){
        return accountRepository.findAll();
    }
    @QueryHandler
    public AccountStatement on(GetAccountStatement query){
        Account account = accountRepository.findById(query.getAccountId()).get();
        List<Operation> operations = operationRepository.findByAccountId(query.getAccountId());
        return new AccountStatement(account, operations);
    }

    @QueryHandler
    public AccountEvent on(WatchEventQuery query){
        return AccountEvent.builder().build();
    }


}
