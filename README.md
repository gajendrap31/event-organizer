# Event Organizer

Backend APIs for an Event Booking System built with Spring Boot.

## Planning

I split the implementation into these parts:

1. Authentication and user roles
2. Event management APIs for organizers
3. Event browsing and booking APIs for customers
4. JWT based request authentication
5. Role based authorization
6. Async background tasks for booking confirmation and event update notification
7. README documentation and demo flow

The first priority was authentication because every protected API depends on knowing the logged-in user and their role. After that, event APIs were added because bookings depend on events. Booking and async notification were implemented after the event flow was ready.

## Design Decisions

### User Types

The system supports two roles:

- `ROLE_ORGANIZER`
- `ROLE_CUSTOMER`

I used separate signup endpoints instead of asking for role in the request body:

```http
POST /signup/organizer
POST /signup/customer
```

This avoids allowing users to send arbitrary role values such as `ROLE_ADMIN`. The backend decides the role based on the endpoint.

### Authentication

Login uses email and password.

Passwords are stored using `BCryptPasswordEncoder`.

On successful login, the backend generates a JWT and returns it in the response body:

```json
{
  "message": "jwt_token_here"
}
```

### JWT Transport

The API uses the `Authorization` header:

```http
Authorization: Bearer <jwt>
```

Cookies were not used because this backend is designed as a simple stateless REST API.

### Authorization

Spring Security is configured as stateless:

```java
SessionCreationPolicy.STATELESS
```

Public endpoints:

- signup
- login
- browse events
- view event details

All other APIs require authentication.

Role access is handled using method-level security:

```java
@PreAuthorize("hasRole('ORGANIZER')")
@PreAuthorize("hasRole('CUSTOMER')")
```

This keeps route protection close to the controller method that needs it.

### Event Management

Only organizers can create, update, delete, and view their own events.

Each event stores:

- title
- description
- location
- event date
- total tickets
- available tickets
- organizer
- created time
- updated time

When an event is created, `availableTickets` is set equal to `totalTickets`.

When an event is updated, the system keeps already booked tickets safe. The updated `totalTickets` cannot be lower than the number of tickets already booked.

Events with bookings cannot be deleted. This avoids orphaned booking history.

### Booking

Only customers can book tickets.

When booking tickets:

- event must exist
- requested ticket count must be available
- available tickets are reduced
- booking is saved
- booking confirmation async task is triggered

Customers can view their own bookings using:

```http
GET /bookings/my
```

### Background Tasks

Spring `@Async` was used for background processing.

This was chosen because the assignment only needs simple async behavior and console logging. A full queue such as RabbitMQ or Kafka would be unnecessary for this scope.

Background Task 1:

- Triggered after successful booking
- Prints booking confirmation email log

Background Task 2:

- Triggered after event update
- Finds all bookings for the event
- Prints event update notification log for each booked customer

### Error Handling

Global exception handling is centralized in `GlobalExceptionHandler`.

Most handlers use a shared `buildErrorResponse` method so error responses follow the same format:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "...",
  "path": "..."
}
```

### Database Design

The project uses JPA entities:

- `User`
- `Event`
- `Booking`

Relationships:

- One organizer can create many events
- One customer can create many bookings
- One event can have many bookings

### Security Choices

CSRF is disabled because the API is stateless and uses JWT through the `Authorization` header.

CORS allows local frontend development origins:

```text
http://localhost:*
http://127.0.0.1:*
```

### AI Tool Usage

AI assistance was used to plan, refactor, and implement the backend APIs, security flow, async tasks, and README documentation.

## Features

- Customer signup and login
- Event organizer signup and login
- JWT based authentication
- Role based API access
- Organizers can create, update, delete, and view their events
- Customers can browse events and book tickets
- Async booking confirmation log
- Async event update notification log

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT

## Run Project

Update database details in `src/main/resources/application.properties`.

```bash
.\mvnw.cmd spring-boot:run
```

Application runs on:

```text
http://localhost:8080
```

## Compile

```bash
.\mvnw.cmd -DskipTests compile
```

## Authentication

Login returns a JWT in the `message` field.

Use the JWT in protected APIs:

```http
Authorization: Bearer <jwt>
```

## Signup Organizer

```http
POST /signup/organizer
Content-Type: application/json
```

```json
{
  "name": "Organizer One",
  "email": "organizer@test.com",
  "password": "password123"
}
```

## Signup Customer

```http
POST /signup/customer
Content-Type: application/json
```

```json
{
  "name": "Customer One",
  "email": "customer@test.com",
  "password": "password123"
}
```

## Login

```http
POST /login
Content-Type: application/json
```

```json
{
  "email": "organizer@test.com",
  "password": "password123"
}
```

Response:

```json
{
  "message": "jwt_token_here"
}
```

## Event APIs

Public APIs:

```http
GET /events
GET /events/{eventId}
```

Organizer APIs:

```http
GET /events/my
POST /events
PUT /events/{eventId}
DELETE /events/{eventId}
```

Create or update event request:

```json
{
  "title": "Music Fest",
  "description": "Live music event",
  "location": "Bangalore",
  "eventDate": "2026-06-20T18:00:00",
  "totalTickets": 100
}
```

## Booking APIs

Customer APIs:

```http
POST /events/{eventId}/bookings
GET /bookings/my
```

Booking request:

```json
{
  "ticketCount": 2
}
```

## Role Access

- `ROLE_ORGANIZER`
  - Can create, update, delete, and view own events
- `ROLE_CUSTOMER`
  - Can book tickets and view own bookings
- Public users
  - Can view event list and event details

## Background Tasks

Spring async processing is enabled using `@EnableAsync`.

When a customer books tickets, the app logs:

```text
Sending booking confirmation email to customer@test.com for event Music Fest
```

When an event is updated, the app logs notifications for customers who booked that event:

```text
Sending event update notification to customer@test.com for event Music Fest Updated
```

## Demo Flow

1. Signup organizer using `POST /signup/organizer`
2. Login organizer using `POST /login`
3. Create event using `POST /events` with organizer JWT
4. Signup customer using `POST /signup/customer`
5. Login customer using `POST /login`
6. Browse events using `GET /events`
7. Book tickets using `POST /events/{eventId}/bookings` with customer JWT
8. Check console for booking confirmation log
9. Update event using `PUT /events/{eventId}` with organizer JWT
10. Check console for event update notification log
