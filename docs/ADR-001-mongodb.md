# ADR-001: Use MongoDB as the persistence layer

## Status

Accepted

## Context

MyPersonalScheduler needs to persist users, notes, tasks and events.

The domain model defines `Task` and `Event` as specialized forms of `Note`.
They share the same identity (`noteId`) and inherit the common properties of a note.

The project also aims to remain relatively simple to develop and maintain while
providing a professional backend architecture.

## Decision

MongoDB will be used as the persistence database through Spring Data MongoDB.

Notes will be stored in a single `notes` collection.

`Task` and `Event` will extend `Note` in the Java domain model rather than being
modeled as independent entities with their own identifiers.

Each note will contain the `userId` of its owner instead of using a database-level
relationship or a MongoDB DBRef.

## Rationale

MongoDB is a suitable choice for the project's document-oriented data model and
provides straightforward integration with Spring Boot through Spring Data MongoDB.

An important advantage for this particular domain is Spring Data MongoDB's support
for type mapping. When storing objects belonging to a class hierarchy, Spring Data
MongoDB can store type information in the `_class` field. This allows the mapping
layer to distinguish between different concrete types when documents are read back.

This is particularly useful for MyPersonalScheduler because a `notes` collection
can contain ordinary `Note` instances as well as `Task` and `Event` instances,
while preserving their Java type information.

The application therefore keeps the conceptual model:

Note
├── Note
├── Task
└── Event

without requiring separate collections or independent identifiers for Task and Event.

Using `userId` inside each note also keeps notes independently stored from their
owner. This makes querying, filtering and bulk deletion of a user's notes
straightforward and avoids unnecessary document references.

## Consequences

### Positive

- Simple integration with Spring Boot through Spring Data MongoDB.
- Natural representation of the application's note-oriented data.
- Supports the `Note` / `Task` / `Event` inheritance model.
- No additional identifiers are required for `Task` or `Event`.
- Notes can be efficiently queried by `userId`.
- Notes remain independent documents from their owner.
- The model can evolve without requiring a rigid relational schema.

### Negative

- Referential integrity between `User` and `Note` must be enforced by application
  logic rather than a relational foreign key.
- User deletion must explicitly handle the user's notes.
- MongoDB's schema flexibility requires validation through application logic.

## References

Spring Data MongoDB documentation:

- Object Mapping
- Type Mapping
- Document References