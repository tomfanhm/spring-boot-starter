# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
make run              # Run with dev profile (requires Docker DB + JWT_SECRET env var)
make test             # Run all tests (requires Docker for Testcontainers)
make build            # Build bootJar
make lint             # spotlessCheck + checkstyleMain + checkstyleTest
make format           # Auto-format with Spotless (Google Java Format)
make docker-up        # Start PostgreSQL via Docker Compose
make docker-down      # Stop Docker services

# Run a single test class
./gradlew test --tests "com.starter.app.modules.auth.AuthServiceTest"

# Run a single test method
./gradlew test --tests "com.starter.app.modules.auth.AuthServiceTest.login_WithValidCredentials_ReturnsToken"
```

## Architecture

Spring Boot 3.4 / Java 21 / Gradle Kotlin DSL. Package-by-feature under `com.starter.app`:

- **`modules/`** - Feature packages (auth, user), each with Controller, Service, DTOs (records), and MapStruct mappers
- **`security/`** - Stateless JWT auth: `JwtAuthenticationFilter` (extracts Bearer token) -> `JwtTokenProvider` (JJWT 0.12, HS256) -> `UserPrincipal` (UserDetails impl)
- **`config/`** - SecurityConfig (CSRF off, stateless sessions, public paths), AppProperties (record with `@ConfigurationProperties`), OpenApiConfig, WebConfig
- **`shared/`** - `GlobalExceptionHandler` (@RestControllerAdvice), ApiError record, custom exceptions, PagedResponse<T>

Request flow: Client -> JwtAuthenticationFilter -> SecurityConfig -> Controller -> Service -> Repository -> PostgreSQL

## Key Conventions

- **DTOs are Java records**, not classes. MapStruct mappers are Spring-component interfaces (`componentModel = "spring"`).
- **Entities** use `@PrePersist`/`@PreUpdate` for audit timestamps, `GenerationType.UUID` for IDs.
- **Validation** via Jakarta annotations on request DTOs (`@Valid` in controllers).
- **API prefix**: `/api` (context-path) + `/v1/` (controller-level).
- **Public endpoints** (no JWT): `/v1/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health`.
- **Flyway migrations** in `src/main/resources/db/migration/` with `V{n}__description.sql` naming.
- **Checkstyle**: 120-char line limit, Google-style import ordering (static first, then third-party).
- **Formatting**: Google Java Format via Spotless. Run `make format` before committing.

## Testing Patterns

- **Unit tests**: `@ExtendWith(MockitoExtension.class)` with `@Mock`/`@InjectMocks`, no Spring context.
- **Controller slice tests**: `@WebMvcTest` with MockMvc.
- **Integration tests**: `@SpringBootTest` + `@AutoConfigureMockMvc` + `@ActiveProfiles("test")`, extending `PostgresTestContainerConfig` (shared Testcontainers PostgreSQL 16-alpine container with `@DynamicPropertySource`).

## Git Conventions

Conventional commits: `feat`, `fix`, `docs`, `style`, `refactor`, `test`.
