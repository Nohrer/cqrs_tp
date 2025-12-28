# CQRS Event Sourcing Application

A Spring Boot application implementing **CQRS (Command Query Responsibility Segregation)** and **Event Sourcing** patterns using the Axon Framework for managing bank account operations.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Key Components](#key-components)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Usage Examples](#usage-examples)

## Overview

This application demonstrates a modern approach to building event-driven systems using CQRS and Event Sourcing patterns. It manages bank account operations such as:
- Creating accounts
- Crediting/Debiting accounts
- Updating account status
- Querying account statements and history

## Architecture

### CQRS Pattern

The application separates **write operations (Commands)** from **read operations (Queries)**:

```
┌─────────────────────────────────────────────────────────┐
│                    Client Application                    │
└───────────────┬─────────────────────┬───────────────────┘
                │                     │
        Commands│                     │Queries
                │                     │
        ┌───────▼───────┐    ┌───────▼────────┐
        │   Command     │    │     Query      │
        │     Side      │    │      Side      │
        └───────┬───────┘    └───────▲────────┘
                │                    │
         Events │                    │ Events
                │                    │
        ┌───────▼────────────────────┴────────┐
        │         Event Store (Axon)          │
        └─────────────────────────────────────┘
```

### Command Side (Write Model)

- **Purpose**: Handle write operations and business logic
- **Components**:
  - **Commands**: Intent to change state (CreateAccount, DebitAccount, etc.)
  - **Aggregates**: Domain objects that handle commands and emit events
  - **Command Handlers**: Process commands within aggregates
  - **Events**: Facts about what happened in the system

### Query Side (Read Model)

- **Purpose**: Handle read operations optimized for querying
- **Components**:
  - **Queries**: Requests for information (GetAllAccounts, GetAccountStatement)
  - **Query Handlers**: Process queries and return data
  - **Event Handlers**: Listen to events and update the read database
  - **Repositories**: Data access layer for the read model

### Event Sourcing

Instead of storing just the current state, the system stores all events that led to the current state:

```
Event 1: AccountCreatedEvent (balance: 1000)
Event 2: AccountStatusUpdatedEvent (status: ACTIVATED)
Event 3: AccountDebitedEvent (amount: 100)
Event 4: AccountCreditedEvent (amount: 50)
→ Current State: balance = 950, status = ACTIVATED
```

## Technologies

- **Java 21** - Programming language
- **Spring Boot 4.0.1** - Application framework
- **Axon Framework 4.10.3** - CQRS and Event Sourcing framework
- **Spring Data JPA** - Data persistence
- **H2/PostgreSQL** - Database options
- **Project Reactor 3.7.2** - Reactive programming
- **Lombok** - Boilerplate code reduction
- **SpringDoc OpenAPI** - API documentation
- **Maven** - Build tool

## Project Structure

```
cqrs_event_sourcing/
├── commands/                          # Command Side (Write Model)
│   ├── aggregates/
│   │   └── AccountAggregate.java     # Domain aggregate handling commands
│   ├── commands/                      # Command definitions
│   │   ├── CreateAccountCommand.java
│   │   ├── CreditAccountCommand.java
│   │   ├── DebitAccountCommand.java
│   │   └── UpdateAccountStatusCommand.java
│   └── controllers/
│       └── AccountCommandController.java  # REST API for commands
│
├── commons/                           # Shared components
│   ├── dtos/                         # Data Transfer Objects
│   │   ├── AccountStatement.java
│   │   ├── CreateAccountDTO.java
│   │   ├── CreditAccountDTO.java
│   │   ├── DebitAccountDTO.java
│   │   ├── OperationDTO.java
│   │   └── UpdateAccountStatusDTO.java
│   ├── enums/
│   │   └── AccountStatus.java        # Account states (CREATED, ACTIVATED, SUSPENDED)
│   └── events/                       # Event definitions
│       ├── AccountCreatedEvent.java
│       ├── AccountCreditedEvent.java
│       ├── AccountDebitedEvent.java
│       └── AccountStatusUpdatedEvent.java
│
└── query/                            # Query Side (Read Model)
    ├── controllers/
    │   └── AccountQueryController.java   # REST API for queries
    ├── dtos/
    │   └── AccountEvent.java
    ├── entities/                     # JPA Entities (Read Model)
    │   ├── Account.java
    │   ├── Operation.java
    │   └── OperationType.java
    ├── handlers/
    │   ├── AccountEventHandler.java  # Listens to events and updates read DB
    │   ├── AccountQueryHandler.java  # Handles queries
    │   └── ReplayService.java        # Event replay functionality
    ├── queries/                      # Query definitions
    │   ├── GetAccountStatement.java
    │   ├── GetAllAccounts.java
    │   └── WatchEventQuery.java
    └── repository/                   # Data access layer
        ├── AccountRepository.java
        └── OperationRepository.java
```

## Key Components

### 1. AccountAggregate

The core domain aggregate that:
- Validates business rules
- Handles commands
- Emits events
- Maintains aggregate state through event sourcing handlers

**Example Flow**:
```java
@CommandHandler
public void handleCommand(DebitAccountCommand command) {
    // Validate business rules
    if (!status.equals(AccountStatus.ACTIVATED)) {
        throw new RuntimeException("Account not activated");
    }
    if (command.getAmount() > currentBalance) {
        throw new RuntimeException("Insufficient balance");
    }
    // Emit event
    AggregateLifecycle.apply(new AccountDebitedEvent(command.getId(), command.getAmount()));
}

@EventSourcingHandler
public void on(AccountDebitedEvent event) {
    // Update aggregate state
    this.currentBalance -= event.amount();
}
```

### 2. AccountEventHandler

Listens to events from the command side and updates the read model (query database):

```java
@EventHandler
public void on(AccountCreatedEvent event) {
    Account account = Account.builder()
        .id(event.accountId())
        .balance(event.initialBalance())
        .currency(event.currency())
        .status(event.accountStatus())
        .build();
    accountRepository.save(account);
}
```

### 3. Command Gateway

Used by controllers to send commands to aggregates:

```java
commandGateway.send(new CreateAccountCommand(id, initialBalance, currency));
```

### 4. Query Gateway

Used by controllers to query the read model:

```java
queryGateway.query(new GetAllAccounts(), ResponseTypes.multipleInstancesOf(Account.class));
```

## API Endpoints

### Command Side (Write Operations)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/commands/accounts/create` | Create a new account |
| POST | `/commands/accounts/debit` | Debit an account |
| POST | `/commands/accounts/credit` | Credit an account |
| PUT | `/commands/accounts/updateStatus` | Update account status |
| GET | `/commands/accounts/events/{accountId}` | Get event stream for account |

### Query Side (Read Operations)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/query/accounts/all` | Get all accounts |
| GET | `/query/accounts/statement/{accountId}` | Get account statement |
| GET | `/query/accounts/watch/{accountId}` | Watch account events (SSE) |

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- (Optional) PostgreSQL database

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd cqrs_event_sourcing
   ```

2. **Build the project**
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - Application: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console` (if using H2)

### Configuration

Configure database settings in `application.properties`:

```properties
# H2 Database (default)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver

# Or PostgreSQL
# spring.datasource.url=jdbc:postgresql://localhost:5432/cqrs_db
# spring.datasource.username=postgres
# spring.datasource.password=your_password
```

## Usage Examples

### 1. Create an Account

```bash
curl -X POST http://localhost:8080/commands/accounts/create \
  -H "Content-Type: application/json" \
  -d '{
    "initialBalance": 1000.0,
    "currency": "USD"
  }'
```

Response: `"<account-id>"`

### 2. Activate the Account

```bash
curl -X PUT http://localhost:8080/commands/accounts/updateStatus \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "<account-id>",
    "accountStatus": "ACTIVATED"
  }'
```

### 3. Debit the Account

```bash
curl -X POST http://localhost:8080/commands/accounts/debit \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "<account-id>",
    "amount": 150.0
  }'
```

### 4. Query All Accounts

```bash
curl http://localhost:8080/query/accounts/all
```

### 5. Get Account Statement

```bash
curl http://localhost:8080/query/accounts/statement/<account-id>
```

### 6. Watch Account Events (Server-Sent Events)

```bash
curl -N http://localhost:8080/query/accounts/watch/<account-id>
```

## Key Benefits

### CQRS Benefits
- **Scalability**: Scale read and write operations independently
- **Performance**: Optimize read and write models separately
- **Flexibility**: Different data models for different purposes

### Event Sourcing Benefits
- **Audit Trail**: Complete history of all changes
- **Time Travel**: Reconstruct state at any point in time
- **Event Replay**: Rebuild read models from events
- **Debugging**: Full visibility into what happened

### Axon Framework Benefits
- **Built-in Event Store**: Persistent event storage
- **Message Routing**: Automatic command/query/event routing
- **Saga Support**: Complex business transaction orchestration
- **Scalability**: Distributed command and query handling

## Additional Resources

- [Axon Framework Documentation](https://docs.axoniq.io/)
- [CQRS Pattern](https://martinfowler.com/bliki/CQRS.html)
- [Event Sourcing Pattern](https://martinfowler.com/eaaDev/EventSourcing.html)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)


---

**Note**: This application uses Axon Server by default for event storage. For production deployments, consider configuring appropriate persistence and event store settings.
