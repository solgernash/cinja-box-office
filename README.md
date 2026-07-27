# CINJA Cinema Booking System

## Overview

CINJA Cinema Booking System is a web-based cinema e-ticket booking application developed for CSCI 4050/6050 Software Engineering. The system is designed for a single theater with multiple showrooms and supports movie browsing, showtime scheduling, seat selection, ticket purchasing, user account management, and administrator management features.

The frontend, **Cinema Ninja**, is a ninja-themed vanilla HTML/CSS/JS single page that talks to a Spring Boot + MongoDB backend. Guests can browse movies, search, filter, and preview a booking without an account; registered users can complete checkout, manage their profile (address, saved payment cards, favorites), and admins get a portal for adding movies and scheduling showtimes.

## Team Members

- Chloe Malimban
- Ido Niv
- Nash Carroll
- Joshua Lumogdang
- Abhinav Aravind

## Project Status

Sprint 1–3 requirements are implemented and wired end-to-end against the real backend. Work remaining is scoped to the final demo (see below).

### Implemented

- Cinema Ninja homepage, movie grid (Currently Running / Coming Soon), fuzzy search, genre filter, movie details modal with trailer
- User registration, login, logout, forgot/reset password
- Profile management: edit name, address, up to 3 saved payment cards, favorite movies
- Real showtimes per movie, fetched from the backend (not hardcoded)
- Booking flow: pick a showtime → enter ticket counts (adult/child/senior) → Start Booking → real seat map → seat selection (capped to ticket count, taken seats blocked) → Continue to Checkout
- Checkout: requires login (guests can select seats first, login is required before checkout continues), Order Summary page (movie, showtime, seats, ticket lines, total), confirm/edit email, mock payment page, booking confirmation email
- Admin portal: Manage Movies (add movie, appears immediately in the customer grid) and Manage Showtimes (schedule a showtime into one of 3 seeded showrooms, with conflict prevention on same showroom/date/time)
- Design pattern: Builder (`BookingBuilder`) used by `BookingService` to construct `Booking` objects

### Not Yet Implemented (final demo scope)

- Order history view for registered users
- Real payment step
- Second design pattern
- Admin Manage Users / Manage Promotions

## Running the Project Locally

The frontend and backend must both be running at the same time. Use two terminal windows: one for the Spring Boot backend and one for the static frontend.

### Prerequisites

- Java 21 (see `<java.version>` in `pom.xml`)
- Maven (or just use the included `./mvnw` wrapper — no local Maven install required)
- Python 3
- A web browser
- Network access to the configured MongoDB Atlas cluster (credentials are in `backend/src/main/resources/application.properties`)

### Start the Backend

From the project root, run:

    ./mvnw spring-boot:run

The backend starts on port **8081** (set in `application.properties`, not the Spring Boot default of 8080).

Test it:

    http://localhost:8081/api/movies

If MongoDB is reachable, this returns JSON movie data.

If port 8081 is already in use (e.g. from a previous run), find and stop it:

    lsof -i :8081
    kill <PID>

or run this instance on another port:

    ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8092

(and update the `*_API_URL` constants at the top of `frontend/app.js` to match).

### Start the Frontend

Open a second terminal:

    cd frontend
    python3 -m http.server 5173

Then open:

    http://localhost:5173

The frontend **must** be served over `http://localhost:...` (not opened as a `file://` path) — booking and checkout rely on a session cookie that requires a real origin.

### Seed / Test Accounts

Seed data is created automatically on first run (`DataSeeder`) if the relevant collections are empty:

| Role     | Email                    | Password      |
|----------|--------------------------|---------------|
| Admin    | admin1@cinema.com        | Admin1123!    |
| Admin    | admin2@cinema.com        | Admin2123!    |
| Customer | john.smith@gmail.com     | Password123!  |
| Customer | sarah.johnson@gmail.com  | Password456!  |

Seeded data also includes 1 theatre, 3 showrooms (30 seats each), 10 movies, and 3 pre-scheduled shows.

## Architecture

Layered / MVC-style separation:

    backend/src/main/java/com/cinema/
      controller/   REST endpoints (thin — request/response mapping only)
      service/      Business logic (booking, checkout, seats, showtimes, users, email...)
      repository/   Spring Data MongoDB repositories
      model/        Domain/document classes
      pattern/      Design pattern implementations (Builder, Facade)
      config/       Spring configuration (DataSeeder, MongoConfig)

    frontend/
      index.html    Markup + all dialogs/panels
      app.js        All frontend logic (fetch calls, rendering, event handling)
      styles.css    Styling

### Design Patterns

- **Builder** — `pattern/BookingBuilder.java`, used by `BookingService.createBooking(...)` to assemble `Booking` objects field-by-field instead of a large telescoping constructor.
- **Facade** — `pattern/CheckoutFacade.java` (in progress — see "Not Yet Implemented" above). Intended to give the checkout controller a single `checkout(customer, card)` entry point that internally coordinates `PaymentCardService`, `PaymentService`, `BookingService`, `TicketService`, and `EmailService`.

### Notable backend detail

`Show.showTime` is a `java.sql.Time`. Spring Data MongoDB can write it but has no built-in converter to read it back (`ConverterNotFoundException`), so `backend/src/main/java/com/cinema/config/MongoConfig.java` registers explicit `Date <-> java.sql.Time` converters. Without this, any endpoint that reads `Show` documents back from MongoDB (showtime lookups, scheduling conflict checks) fails with a 500.

## Course Information

CSCI 4050/6050 – Software Engineering
Cinema E-Booking System Term Project
