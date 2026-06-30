# CINJA Cinema Booking System

## Overview

CINJA Cinema Booking System is a web-based cinema e-ticket booking application developed for CSCI 4050/6050 Software Engineering. The system is designed for a single theater with multiple halls and supports movie browsing, showtime selection, seat selection, ticket purchasing, user account management, and administrator management features.

The current Sprint 1 implementation includes a custom frontend called **Cinema Ninja**, styled around the CINJA team name and a ninja-inspired cinema theme. The application currently allows users to browse movies pulled from MongoDB through a Spring Boot backend, search and filter movie listings, view movie details and trailers, select showtimes, choose ticket quantities, select seats, and preview a checkout-style booking flow.

The application allows guests to browse and search for movies, while authenticated users will eventually be able to complete checkout, manage profiles, view order history, save favorite movies, and receive movie recommendations.

## Team Members

- Chloe Malimban
- Ido Niv
- Nash Carroll
- Joshua Lumogdang
- Abhinav Aravind

## Project Status

This repository is currently under development for a Software Engineering term project.

### Implemented So Far

- Custom Cinema Ninja homepage
- Ninja-themed frontend design and animations
- Movies loaded dynamically from MongoDB through the Spring Boot backend
- Movie cards separated into Currently Running and Coming Soon sections
- Fuzzy search for movie titles and genres
- Genre filtering
- Show date filter UI placeholder
- Movie details modal
- Embedded trailer playback
- Showtime buttons
- Booking prototype section
- Smooth scrolling from selected showtime to booking section
- Ticket quantity selection for Child, Adult, and Senior tickets
- Automatic ticket total calculation
- Seat selection UI
- Selected seats display
- Reset of ticket and seat selections when a new showtime is selected

### Not Yet Implemented

- Full checkout and payment logic
- User registration and login
- User profile management
- Saved payment methods
- Order history
- Favorites
- Recommendations
- Admin dashboard
- Admin movie and showtime management
- Backend booking persistence
- Real payment processing

## Running the Project Locally

The frontend and backend must both be running at the same time. Use two terminal windows: one for the Spring Boot backend and one for the static frontend.

### Prerequisites

Make sure you have the following installed:

- Java
- Maven
- Python
- A web browser
- Access to the configured MongoDB database

### Start the Backend

From the project root, run:

    mvn spring-boot:run

The backend should start on port `8080`.

Once the backend is running, test the movie API in your browser:

    http://localhost:8080/api/movies

If the backend is connected to MongoDB correctly, this should return JSON movie data.

### Start the Frontend

Open a second terminal. From the project root, go into the frontend folder:

    cd frontend

Start a local static server:

    python -m http.server 5173

If your system uses `python3`, run:

    python3 -m http.server 5173

Then open the frontend in your browser:

    http://localhost:5173

The Cinema Ninja homepage should load and fetch movies from the Spring Boot backend.

### Expected Local Setup

When running locally, you should have:

    Backend:  http://localhost:8080
    Frontend: http://localhost:5173
    Movie API: http://localhost:8080/api/movies

The frontend API URL is configured in `frontend/app.js`:

    const MOVIES_API_URL = "http://localhost:8080/api/movies";

### If Port 8080 Is Already in Use

If Spring Boot fails with a message saying port `8080` is already in use, check what is using the port:

    lsof -i :8080

or:

    ss -ltnp 'sport = :8080'

If the output shows a process ID, stop it:

    kill PID_NUMBER

If it does not stop:

    kill -9 PID_NUMBER

Then rerun:

    mvn spring-boot:run

### Running the Backend on a Different Port

If you do not want to stop the process using `8080`, run Spring Boot on port `8081`:

    mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

Then update the frontend API URL in `frontend/app.js`:

    const MOVIES_API_URL = "http://localhost:8081/api/movies";

Test the new backend URL:

    http://localhost:8081/api/movies

## Common Local Issues

### Movies Are Not Showing

Open the backend movie API directly:

    http://localhost:8080/api/movies

If it does not return JSON, the backend is not running correctly, the API path is wrong, or the MongoDB connection is not working.

Also check the browser developer console for errors.

### CORS Error

If the frontend console shows a CORS error, make sure the Spring Boot movie controller allows the frontend origin:

    @CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
    })

The frontend origin is the URL where the frontend is running, not the backend URL.

### Backend 404 Error

If this returns 404:

    http://localhost:8080/api/movies

then the backend route is not available. Check the movie controller and confirm it maps to:

    @RequestMapping("/api/movies")

and has a `GET` method for returning all movies.

### Frontend Still Shows Old Files

Hard refresh the browser:

    Ctrl + Shift + R

Browsers may cache JavaScript and CSS during local development.

## Project Features

### Movie Browsing and Search

Users can browse currently playing and coming-soon movies. The system supports searching and filtering movies by title, category, and show date.

The current frontend supports:

- Browsing movie cards
- Fuzzy search by title or genre
- Filtering by genre
- Show date filter UI placeholder
- Viewing movie details
- Playing embedded trailers

### Ticket Selection

Users can select a movie showing, choose ticket quantities, assign age categories, and select seats using a graphical theater hall layout.

Supported ticket categories include:

- Child
- Adult
- Senior

The current booking section is a frontend prototype only. Full backend booking and checkout logic will be implemented in later sprints.

### User Accounts

The system will support user registration, authentication, password recovery, and profile management. Registered users will be able to manage personal information, preferences, promotion subscriptions, addresses, and saved payment methods.

### Checkout and Orders

Authenticated users will be able to complete checkout and payment. The system will calculate total order price using ticket prices, taxes, booking fees, and promotional discounts. Users will also be able to view their order history.

### Favorites and Recommendations

Authenticated users will be able to add movies to a Favorites list and receive movie recommendations based on favorites, preferences, or summarized system activity.

### Administrator Features

Administrators will be able to manage:

- Movie listings
- Show dates and times
- Ticket prices
- Online booking fees
- Promotional offers
- User accounts
- Administrator accounts
- Promotional communications
- Real-time booking, sales, movie, and user statistics

## Current Architecture

The project currently uses a separated frontend/backend structure.

    cinja-box-office/
      frontend/
        index.html
        styles.css
        app.js

      backend/
        src/main/java/com/cinema/
          controller/
          model/
          repository/
          service/

        src/main/resources/
          application.properties

      pom.xml

### Frontend

The frontend is currently written with:

- HTML
- CSS
- Vanilla JavaScript

The frontend is responsible for:

- Rendering the Cinema Ninja homepage
- Fetching movie data from the backend
- Displaying movies
- Search and filtering
- Movie details modal
- Trailer embeds
- Booking prototype UI
- Ticket quantity controls
- Seat selection
- Scroll animations

### Backend

The backend is a Spring Boot application that connects to MongoDB.

The backend is responsible for:

- Connecting to MongoDB
- Exposing movie data through REST endpoints
- Returning movie records to the frontend

Current movie endpoint:

    GET http://localhost:8080/api/movies

### Database

The project currently uses MongoDB for movie storage.

Example movie document:

    {
      "title": "Shadow Circuit",
      "runtime": "2h 04m",
      "genre": "Action",
      "rating": "PG-13",
      "description": "A rogue projectionist uncovers a secret clan hiding clues inside blockbuster trailers.",
      "posterUrl": "https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=900&q=80",
      "trailerUrl": "https://www.youtube.com/embed/dQw4w9WgXcQ",
      "status": "RUNNING",
      "showtimes": [
        "2:00 PM",
        "5:00 PM",
        "8:00 PM"
      ]
    }

The frontend normalizes backend movie data so that statuses like `RUNNING` display as `Currently Running`.

## Technical Requirements

The system is designed to support:

- Multi-user access
- Authentication and authorization
- Role-based access control
- Secure handling of passwords and payment data
- Persistent data storage
- Object-oriented programming principles
- Separation of concerns using MVC or a similar multi-layer architecture
- Browser-based access through common web browsers

## System Roles

### Guest User

A guest user can browse movies, search listings, view movie information, watch trailers, and preview the booking flow.

### Registered User

A registered user will be able to manage an account, select seats, purchase tickets, view order history, manage favorites, and receive recommendations.

### Administrator

An administrator will be able to manage movies, showtimes, ticket prices, promotions, users, and system statistics.

## Planned Architecture

The project will follow a multi-layered architecture aligned with MVC principles:

- Presentation Layer: User interface and browser-based views
- Application/Controller Layer: Request handling and business workflow logic
- Service Layer: Core business logic for bookings, payments, users, movies, and recommendations
- Data Access Layer: Persistent database interaction
- Database Layer: Storage for users, movies, tickets, orders, payments, promotions, and theater halls

## Security Considerations

The system will enforce authentication and authorization for protected features. Sensitive data such as passwords and payment information will be handled securely using accepted web application security practices.

MongoDB credentials should not be committed to a public repository. Use environment variables or local configuration for secrets when possible.

The recommendation service will avoid transmitting personal identifiers, payment data, or raw transaction records. Only summarized, non-identifiable information will be shared when needed.

## Course Information

CSCI 4050/6050 – Software Engineering  
Cinema E-Booking System Term Project
