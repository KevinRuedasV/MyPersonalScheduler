# ADR-004: JWT Authentication and API Security

## Status

Accepted

## Date

2026-08-18

## Context

MyPersonalScheduler contains private user data, including notes, tasks,
events and in-app reminders.

Until this point, the API has identified the user through the `X-User-Id`
HTTP header. This mechanism is not secure because the client can freely
choose the value of that header.

The application therefore requires a real authentication mechanism before
the API can be considered secure.

The MVP requires:

- User registration.
- User login.
- Authentication of protected API requests.
- Verification that the authenticated user exists.
- Authorization based on resource ownership.
- No roles or permissions system yet.
- No server-side sessions.
- Access tokens only.
- No refresh tokens for the MVP.

## Decision

The application will use JSON Web Tokens (JWT) as bearer access tokens.

The JWT will be signed using HMAC SHA-256 (HS256).

The signing secret:

- Must never be hardcoded in source code.
- Must never be committed to Git.
- Must be supplied through environment configuration.
- Must contain sufficient entropy and length for HS256.
- Must only be known by the backend.

Access tokens will have a lifetime of 15 minutes.

The login flow will be:

1. Client sends email and password to `/api/users/login`.
2. The server verifies the credentials using the existing BCrypt
   password hash.
3. The server generates a signed JWT.
4. The server returns the access token to the client.
5. The client sends the token in the HTTP `Authorization` header:

   `Authorization: Bearer <token>`

6. The backend validates the token before allowing access to protected
   endpoints.

## JWT contents

The JWT will contain only information required for authentication and
authorization.

The subject (`sub`) will contain the user's `userId`.

The token will also contain standard claims such as:

- `iat`: token creation time.
- `exp`: token expiration time.
- `jti`: unique token identifier.

The user's identity is ultimately confirmed through the signed `sub`
claim and the existence of that user in the database.

## Token validation

For every protected request, the backend must:

1. Read the `Authorization: Bearer ...` header.
2. Verify the JWT signature using the server-side secret.
3. Verify that the token uses the expected signing algorithm.
4. Verify the token is structurally valid.
5. Verify the expiration time.
6. Extract the `sub` claim.
7. Confirm that the corresponding user still exists.
8. Create the authenticated security context.
9. Execute the controller.

Invalid, expired, malformed or unverifiable tokens must result in
authentication failure.

A client must never be able to select its own authenticated `userId`.

## Authorization

Authentication and authorization are separate concerns.

A valid JWT proves that the request originates from a client possessing
a token issued for a particular user.

It does not automatically grant access to every resource.

Services must continue verifying resource ownership.

For user-owned resources, authentication alone is not sufficient.

The authenticated userId obtained from the SecurityContext must be
compared with the owner of the requested resource whenever the endpoint
operates on a user-specific resource.

A client-supplied userId must never override the authenticated identity.

For example, note operations will continue using the authenticated
user's `userId` and will verify that the requested note belongs to that
user.

The client will no longer provide `X-User-Id`.

## Protected endpoints

The following endpoints remain public:

- `POST /api/users/register`
- `POST /api/users/login`

User-specific and application-data endpoints will require authentication.

This includes:

- `/api/users/{userId}`
- `/api/notes/**`
- `/api/reminders/**`

The application will not introduce roles in the MVP.

## User identity inside controllers

Controllers must obtain the authenticated user identity from Spring Security
rather than from request headers.

The `X-User-Id` header will therefore be removed from the API.

The authenticated principal will contain the server-validated `userId`.

## CORS

During local development, CORS will allow:

`http://localhost:4200`

This configuration is explicitly development-oriented.

Before production deployment, the allowed origin must be changed to the
real frontend domain.

CORS configuration must never be used as an authentication mechanism.

## Stateless security

The API will use stateless authentication.

The server will not maintain login sessions.

Each protected request must carry its access token.

The MVP intentionally does not implement refresh tokens.

The expected flow is:

`login -> access token -> 15 minutes -> login again`

## Access token expiration and atomicity

JWT expiration does not interrupt an HTTP request that has already reached
the backend with a valid token.

Token validation occurs before the protected operation begins.

Once a request has been authenticated successfully, expiration occurring
after validation does not partially interrupt the business operation.

Business operations must continue to rely on normal service-layer
transactional and persistence semantics.

The MVP will not introduce token refresh or long-lived sessions.

## Password security

Passwords will continue to be stored using BCrypt.

Only the BCrypt hash is stored in MongoDB.

Plain-text passwords must never be persisted, returned by the API, logged,
or included in JWTs.

The JWT secret and password hashes are separate security mechanisms:

- BCrypt protects stored user passwords.
- HS256 protects the authenticity of JWTs.

## Threats addressed

This architecture prevents a client from simply changing:

`X-User-Id: another-user`

because that header will no longer be trusted.

It also prevents a client from modifying the JWT subject without invalidating
the JWT signature.

A forged or modified token must therefore fail signature validation.

An attacker possessing no valid signing secret cannot generate a valid token
for another user.

## Threats not addressed yet

The MVP intentionally does not implement:

- Refresh tokens.
- Token revocation lists.
- Password reset.
- Email verification.
- Multi-factor authentication.
- Account lockout.
- Rate limiting.
- Roles.
- Permissions.
- Device/session management.
- Advanced audit logging.

These may be considered in future iterations.

## Consequences

### Positive

- API endpoints become genuinely authenticated.
- User identity is determined server-side.
- Clients cannot choose arbitrary user IDs.
- JWT validation is stateless.
- The system remains simple enough for the MVP.
- The architecture can later support refresh tokens, roles and more
  advanced security mechanisms.

### Negative

- Users must authenticate again after the 15-minute access token expires.
- The backend performs a user lookup when validating authentication.
- JWT secret management becomes part of the deployment configuration.
- Security configuration adds complexity to the backend.

## Future considerations

When the application moves beyond the MVP, the security layer should be
extended with:

- Refresh tokens.
- Token revocation.
- Password reset.
- Email verification.
- Rate limiting.
- Brute-force protection.
- Security auditing.
- Production CORS configuration.
- HTTPS enforcement.
- More sophisticated authorization.

The current architecture is intentionally designed so these features can
be added without replacing the fundamental authentication mechanism.