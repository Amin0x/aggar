# API authentication

The API is stateless and accepts signed HS256 JWTs in the `Authorization: Bearer` header. Set `app.security.jwt.secret` to a cryptographically random value of at least 32 UTF-8 bytes in the API service's ignored `.env` file. The service intentionally refuses to start if it is missing or too short.

For local PowerShell development, create `aggar-api-service/.env`:

```properties
app.security.jwt.secret=<random-secret-at-least-32-bytes>
```

Spring Boot loads this file automatically when the API starts from its service directory. Do not commit or share it. `POST /api/users/authenticate` returns an access token with a 30-minute lifetime. The Thymeleaf frontend keeps it server-side in the HTTP session and forwards it to the API; it does not expose the token to browser JavaScript. In HTTPS deployments, set `SESSION_COOKIE_SECURE=true` in the frontend environment.

Public routes are registration/authentication, public browsing (states, cities, neighborhoods, amenities, property details by ID or slug regardless of status, and visible comments), and images for public properties. Other mutations require authentication. Catalog edits, price-history access, user administration, and moderation require `ROLE_ADMIN`; property owners/agents can manage their own listings and images and read their property messages. Only signed-in users can post comments or send messages. Registration always creates a non-admin `owner` account.
