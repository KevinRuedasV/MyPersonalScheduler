# ADR-003 — User Authentication and Password Storage

## Status

Accepted

## Date

2026-08-17

## Context

MyPersonalScheduler requires a basic user management system for the MVP.

Users need to be able to register and authenticate themselves before the complete security layer is introduced.

The system must therefore define:

- which user attributes are mandatory;
- which attribute identifies a user during authentication;
- how passwords are stored;
- how usernames behave;
- how the current authentication mechanism can be replaced or extended later.

## Decision

### Registration

User registration requires:

- `email`
- `password`

The `username` is optional.

If no username is provided, the system uses the user's email as the username.

Usernames are not unique and have no identification or authentication responsibility. They are only a nickname displayed to the user.

### Authentication

Authentication is performed exclusively using:

- email
- password

Username-based authentication is explicitly not supported.

The email is normalized before storage and authentication, using trimming and lowercase normalization.

Emails must be unique.

### Password storage

Passwords must never be stored in plaintext.

The system uses BCrypt to generate a password hash.

Only the resulting `passwordHash` is stored in MongoDB.

The password hash must never be exposed through API responses or DTOs.

The BCrypt configuration itself must not expose secrets or sensitive credentials.

### Invalid credentials

When authentication fails, the system returns the same public error regardless of whether:

- the email does not exist; or
- the password is incorrect.

This prevents the API from revealing whether a particular email is registered.

### Extensibility

The authentication implementation is intentionally simple for the MVP.

The service layer is separated from the controller layer so that a future security implementation can replace or extend the current mechanism without requiring a redesign of the user domain model.

Future security features may include:

- Spring Security;
- authenticated request contexts;
- JWT or session-based authentication;
- refresh tokens;
- authorization and roles;
- account protection mechanisms.

These features are outside the scope of the current MVP implementation.

## Consequences

### Positive

- Email provides a stable and unique authentication identifier.
- Usernames remain simple presentation data.
- Passwords are never stored in plaintext.
- Password hashes are never exposed through the API.
- The authentication logic is easy to test.
- The design can be extended with a complete security layer later.
- User management does not need to be redesigned when security is introduced.

### Negative

- The current authentication mechanism is not a complete security system.
- There is no token/session management yet.
- Authentication is not yet integrated into protected endpoints.
- Additional security mechanisms will be required before production deployment.

## Alternatives considered

### Username-based login

Rejected because usernames are explicitly non-unique and have no identity responsibility.

### Email and username login

Rejected because usernames are optional and non-unique.

### Plaintext password storage

Rejected because it is fundamentally insecure and must never be used.

### Storing encrypted passwords instead of hashes

Rejected because passwords do not need to be recoverable. A one-way password hash is the appropriate mechanism for password verification.

## Summary

For the MVP, users are identified for authentication by their unique email address.

Passwords are stored exclusively as BCrypt hashes.

Usernames are optional, non-unique nicknames and are not used for authentication.