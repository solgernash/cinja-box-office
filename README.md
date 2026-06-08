# CINJA Cinema Booking System

## Overview

CINJA Cinema Booking System is a web-based cinema e-ticket booking application developed for CSCI 4050/6050 Software Engineering. The system is designed for a single theater with multiple halls and supports movie browsing, showtime selection, seat selection, ticket purchasing, user account management, and administrator management features.

The application allows guests to browse and search for movies, while authenticated users can complete checkout, manage profiles, view order history, save favorite movies, and receive movie recommendations.

## Team Members

- Chloe Malimban
- Ido Niv
- Nash Carroll
- Joshua Lumogdang
- Abhinav Aravind

## Project Features

### Movie Browsing and Search

Users can browse currently playing and coming-soon movies. The system supports searching and filtering movies by title, category, and show date.

### Ticket Selection

Users can select a movie showing, choose ticket quantities, assign age categories, and select seats using a graphical theater hall layout.

Supported ticket categories include:

- Child
- Adult
- Senior

### User Accounts

The system supports user registration, authentication, password recovery, and profile management. Registered users can manage personal information, preferences, promotion subscriptions, addresses, and saved payment methods.

### Checkout and Orders

Authenticated users can complete checkout and payment. The system calculates total order price using ticket prices, taxes, booking fees, and promotional discounts. Users can also view their order history.

### Favorites and Recommendations

Authenticated users can add movies to a Favorites list and receive movie recommendations based on favorites, preferences, or summarized system activity.

### Administrator Features

Administrators can manage:

- Movie listings
- Show dates and times
- Ticket prices
- Online booking fees
- Promotional offers
- User accounts
- Administrator accounts
- Promotional communications
- Real-time booking, sales, movie, and user statistics

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

A guest user can browse movies, search listings, and view movie information.

### Registered User

A registered user can manage an account, select seats, purchase tickets, view order history, manage favorites, and receive recommendations.

### Administrator

An administrator can manage movies, showtimes, ticket prices, promotions, users, and system statistics.

## Planned Architecture

The project will follow a multi-layered architecture aligned with MVC principles:

- Presentation Layer: User interface and browser-based views
- Application/Controller Layer: Request handling and business workflow logic
- Service Layer: Core business logic for bookings, payments, users, movies, and recommendations
- Data Access Layer: Persistent database interaction
- Database Layer: Storage for users, movies, tickets, orders, payments, promotions, and theater halls

## Security Considerations

The system will enforce authentication and authorization for protected features. Sensitive data such as passwords and payment information will be handled securely using accepted web application security practices.

The recommendation service will avoid transmitting personal identifiers, payment data, or raw transaction records. Only summarized, non-identifiable information will be shared when needed.

## Project Status

This repository is currently under development for a Software Engineering term project.

## Course Information

CSCI 4050/6050 – Software Engineering  
Cinema E-Booking System Term Project
