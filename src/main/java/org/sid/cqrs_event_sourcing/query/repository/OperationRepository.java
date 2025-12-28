package org.sid.cqrs_event_sourcing.query.repository;

import net.youssfi.demoaxon.query.entities.Account;
import net.youssfi.demoaxon.query.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation,Long> {
 List<Operation> findByAccountId(String accountId);
}
