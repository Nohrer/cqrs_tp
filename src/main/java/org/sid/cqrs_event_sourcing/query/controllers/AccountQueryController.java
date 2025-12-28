package org.sid.cqrs_event_sourcing.query.controllers;


import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.sid.cqrs_event_sourcing.commons.dtos.AccountStatement;
import org.sid.cqrs_event_sourcing.query.dtos.AccountEvent;
import org.sid.cqrs_event_sourcing.query.entities.Account;
import org.sid.cqrs_event_sourcing.query.queries.GetAccountStatement;
import org.sid.cqrs_event_sourcing.query.queries.GetAllAccounts;
import org.sid.cqrs_event_sourcing.query.queries.WatchEventQuery;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/query/accounts")
@CrossOrigin("*")
public class AccountQueryController {
    private QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/all")
    public CompletableFuture<List<Account>> getAllAccounts(){
        CompletableFuture<List<Account>> result = queryGateway.query(new GetAllAccounts(), ResponseTypes.multipleInstancesOf(Account.class));
        return result;
    }
    @GetMapping("/statement/{accountId}")
    public CompletableFuture<AccountStatement> getAccountStatement(@PathVariable String accountId){
        CompletableFuture<AccountStatement> result = queryGateway.query(new GetAccountStatement(accountId), ResponseTypes.instanceOf(AccountStatement.class));
        return result;
    }

    @GetMapping(value = "/watch/{accountId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AccountEvent> watch(@PathVariable String accountId){
        SubscriptionQueryResult<AccountEvent, AccountEvent> result = queryGateway.subscriptionQuery(new WatchEventQuery(accountId),
                ResponseTypes.instanceOf(AccountEvent.class),
                ResponseTypes.instanceOf(AccountEvent.class)
        );
        return result.initialResult().concatWith(result.updates());
    }


}
