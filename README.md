# DriveDesk – Car Rental Management

A full-stack rental management system built with **Java 17, Spring Boot 3, Spring Data JPA, Spring Security and H2**, with a lightweight web dashboard.

## Features
- Fleet management (add, edit, delete, maintenance status) with validation
- **Availability search** by date range using a JPQL `NOT EXISTS` query
- **Double-booking prevention** (date-overlap check inside a transaction)
- **Pricing engine**: 10% off for 7+ days, 15% off for 30+ days, live quote endpoint
- Booking lifecycle: `ACTIVE → COMPLETED / CANCELLED`
- Dashboard: fleet size, cars rented today, utilisation %, revenue
- Role-protected API: fleet browsing is public, staff actions use HTTP Basic auth (BCrypt)
- Global exception handling with meaningful 400 / 404 / 409 responses
- Unit and integration tests (JUnit 5, MockMvc, Spring Security Test)
- Dockerfile for one-command deployment

## Run
    mvn spring-boot:run
Open http://localhost:8080 — staff login: `admin` / `admin123`
(set `ADMIN_PASSWORD` env var to change it).

    mvn test                                   # run tests
    docker build -t drivedesk . && docker run -p 8080:8080 drivedesk

## API
| Method | Path | Access |
|---|---|---|
| GET | /api/cars, /api/cars/available?start=&end=, /api/cars/{id}/quote?start=&end= | public |
| GET | /api/dashboard | public |
| POST/PUT/DELETE | /api/cars | staff |
| GET/POST | /api/customers | staff |
| GET/POST | /api/bookings, POST /api/bookings/{id}/return, /{id}/cancel | staff |

## Design notes
- Layered structure: controller → service → repository; business rules live in `BookingService` and `PricingService`.
- Car status is only `AVAILABLE` or `MAINTENANCE`; "rented" is derived from bookings and dates, so it can never go out of sync.
- Possible next steps: PostgreSQL + Flyway, JWT auth with roles, pagination, OpenAPI docs, CI with GitHub Actions.
