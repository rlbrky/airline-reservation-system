# Airline Reservation System
> A full-stack flight booking app built to learn RESTful API design, relational modelling, and concurrency-safe transactions — with Spring Boot, Spring Security, Hibernate/JPA, and Thymeleaf.

## Screenshots

### Login
<img width="502" height="88" alt="Login Screen" src="https://github.com/user-attachments/assets/fba70b96-d7ac-4e6c-bcee-8070c107855f" />

### Flight search
<img width="2425" height="500" alt="Flight search results filtered by origin, destination, and date" src="https://github.com/user-attachments/assets/f4faaf23-b8e3-4930-baa0-f35a3727ff35" />

### Seat map
<img width="488" height="771" alt="Interactive seat map with available seats in green and booked seats greyed out" src="https://github.com/user-attachments/assets/44dbd6ca-dbea-457e-a6e7-795bc61cbf53" />

### My Bookings
<img width="958" height="306" alt="My Bookings page listing confirmed bookings with cancel buttons" src="https://github.com/user-attachments/assets/8a6fe0a6-1eb0-436b-a0c2-377377778017" />

### Admin flights management panel
<img width="1012" height="1022" alt="Admin panel with the create-flight form and a list of existing flights" src="https://github.com/user-attachments/assets/d801b0e3-9204-477d-9845-413a08a9902a" />

### Admin airport maangement panel
<img width="997" height="612" alt="Admin panel with create-airport form and a list of existing airports" src="https://github.com/user-attachments/assets/936538c2-f835-4da4-bad2-465d28995f8d" />

### Admin booking management panel
<img width="981" height="386" alt="Admin panel that lets admin cancel anyones booking with a list of all bookings" src="https://github.com/user-attachments/assets/40ac4dbe-3a66-4be8-881f-c0c2848f9ea3" />

## Features
- **User registration and login**
- **Flight search** by origin, destination, and date
- **Interactive seat map** — pick a seat, see availability at a glance
- **Book and cancel** tickets; a "My Bookings" page for each user
- **Admin module** — create flights (seats are generated automatically), manage airports, and view or cancel any booking
- **REST API** exposing the same capabilities for non-browser clients (see below)

## Security & correctness highlights
- **Concurrency-safe booking** — optimistic locking (JPA `@Version`) prevents two users from booking the same seat, backed by a **multi-threaded test** that proves exactly one booking wins the race
- Passwords hashed with **BCrypt** (never stored in plaintext)
- **Two security filter chains** — a session-based chain with **CSRF** for the web app, and a separate **stateless HTTP Basic** chain for `/api/**` (CSRF safely disabled because it carries no session cookie)
- **Role-based** authorization enforced at both the URL and the method level (`@PreAuthorize`)
- Writes are **`@Transactional`**; money is **`BigDecimal`**
- The API never exposes entities — everything maps to **DTO records**, and errors return structured **JSON** via `@RestControllerAdvice`
- Least-privilege DB user; DB password kept out of source via an environment variable

## REST API

All endpoints live under `/api` and require **HTTP Basic** authentication (use an account you registered).

| Method | Path | Description | Success |
|--------|------|-------------|---------|
| `GET` | `/api/flights` | List all flights, or search with `?origin=IST&destination=LHR&date=2026-07-20` | `200` |
| `GET` | `/api/flights/{id}` | Flight details | `200` / `404` |
| `GET` | `/api/flights/{id}/seats` | Seats for a flight | `200` / `404` |
| `POST` | `/api/bookings` | Book a seat — body `{"seatId": 12}` | `201` + `Location` |
| `GET` | `/api/bookings` | The authenticated user's bookings | `200` |
| `DELETE` | `/api/bookings/{ref}` | Cancel a booking by reference | `204` |

Error responses are JSON with the right status code: `400` (invalid input), `401` (no credentials), `404` (not found), `409` (seat already booked / lost the booking race).

```bash
# Book a seat
curl -i -u alice:your-password -X POST http://localhost:8080/api/bookings \
     -H "Content-Type: application/json" -d '{"seatId": 12}'
# → 201 Created
# → Location: /api/bookings/AB3F9K
```

### Seen in Postman
<img width="659" height="862" alt="POST /api/bookings in Postman returning 201 Created with the Location header" src="https://github.com/user-attachments/assets/2dc3efe0-fb69-4eb4-a014-fde3d6b16959" />

<img width="680" height="828" alt="Booking an already-booked seat returns 409 Conflict with a JSON error body" src="https://github.com/user-attachments/assets/26b499e7-bbd5-47e6-aac8-37da4bd3bfd3" />

## Tech stack
Java 26 · Spring Boot 4.1 · Spring Security · Spring Data JPA / Hibernate · Thymeleaf · MySQL · Maven · JUnit 5 / Mockito / AssertJ (tests on in-memory H2)

## Getting started
### Prerequisites
Java 26, MySQL 8, (Maven — or use the included `./mvnw` wrapper)

### 1. Create the database and a scoped user
```sql
CREATE DATABASE airline_reservation
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'airline_user'@'localhost' IDENTIFIED BY 'your-password';
GRANT ALL PRIVILEGES ON airline_reservation.* TO 'airline_user'@'localhost';
```
### 2. Provide the DB password via environment variable
Set `AIRLINE_DB_PASSWORD=your-password` in your environment (or your IDE's run configuration).

### 3. Run
```bash
./mvnw spring-boot:run
```
Then open http://localhost:8080

The `dev` profile is active by default and seeds demo airports, flights, and a dev admin account (`admin` / `admin`) so you can explore the admin module immediately. Register a normal account to book as a user.

### Run the tests
```bash
./mvnw test
```
Tests run against in-memory H2 — no MySQL required. This includes the concurrency test that fires many threads at a single seat and asserts only one booking succeeds.

## Architecture
Two front doors over one service layer:

```
            ┌─ @Controller  (Thymeleaf · session · CSRF)  ─┐
Browser ────┤                                               ├─→ @Service ─→ Repository ─→ MySQL
curl / API ─┴─ @RestController (/api · stateless · Basic) ─┘   (@Transactional)
```

The `@Service` layer holds all business logic and is the single source of truth; both the server-rendered web app and the JSON API call it, so neither duplicates rules. Spring Security runs an ordered pair of filter chains in front, and a `UserDetailsService` bridges the `AirlineUser` entity to authentication. Seat booking flips seat status inside a transaction guarded by an optimistic-lock version column, which is what makes concurrent bookings safe.
