package org.sid.cqrs_event_sourcing.query.repository;

import net.youssfi.demoaxon.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
