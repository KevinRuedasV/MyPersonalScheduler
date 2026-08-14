# ADR-0002: Separation between Service Interface and Implementation

## Status

Accepted

## Context

The application contains a business logic layer responsible for enforcing
domain rules independently of HTTP controllers and persistence repositories.

We want to keep the business logic isolated from transport concerns and allow
the service contract to remain independent from its concrete implementation.

## Decision

The service layer will separate its public contract from its implementation.

For each relevant service:

- An interface will define the operations exposed by the service.
- A concrete `*Impl` class will implement that interface.
- Controllers will depend on the service interface rather than directly on
  the implementation.

For example:

- `NoteService`
- `NoteServiceImpl`

The implementation will depend on repositories and will contain the business
rules associated with notes, tasks and events.

## Consequences

### Positive

- Clear separation of responsibilities.
- Controllers remain independent from implementation details.
- Business logic can be tested independently.
- The service contract becomes explicit.
- Future implementations can be introduced without changing controller code.

### Negative

- Each service requires an additional interface and implementation class.
- For very small services this may introduce some additional boilerplate.

The project accepts this additional structure because maintainability,
testability and architectural clarity are important project goals.