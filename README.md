# MyPersonalScheduler

MyPersonalScheduler is a personal scheduling web application that combines
note-taking and calendar-based organization in a single system.

The application is built around the concept of a note. Notes can remain as
simple text containers or be associated with a date and converted into either
a task or an event.

## Overview

MyPersonalScheduler aims to provide a simple and flexible way to manage
personal information and organize it over time without separating notes,
tasks, and events into completely independent systems.

A note can optionally be associated with a single date and classified as:

- A regular note
- A task
- An event

A note can only have one temporal type at a time. Active tasks and events may
be converted from one type to the other, while completed tasks and celebrated
events become final states.

The application also provides a calendar view where dated notes can be
visualized and an in-app reminder system for relevant dates.

## Main Features

- Create, edit, delete and visualize notes.
- Notes with a title and text body.
- Organize notes using tags.
- Search notes by text.
- Associate notes with a date.
- Convert notes into tasks or events.
- Convert active tasks into events and vice versa.
- Mark tasks as completed.
- Mark events as celebrated.
- Visualize tasks and events in a calendar.
- Navigate between calendar dates.
- Display in-app reminders.
- Manage reminders through an in-app reminder inbox.
- User accounts and private personal data.

## Out of Scope

The initial MVP does not include:

- Collaboration between users.
- Push or device notifications.
- Google Calendar integration.
- Mobile applications.
- Multilingual support.
- Advanced authentication mechanisms.
- External calendar integrations.

These features may be considered for future versions.

## Technology Stack

### Frontend

- Angular
- TypeScript
- HTML
- CSS

### Backend

- Java
- Spring Boot
- Maven
- REST API

### Database

- MongoDB

### Development Tools

- Git
- GitHub
- Visual Studio Code

## Project Structure

The project is divided into two main applications:

```text
MyPersonalScheduler/
├── backend/
│   └── ...
├── frontend/
│   └── ...
├── docs/
│   └── ...
├── .gitignore
├── LICENSE
└── README.md
```

The `frontend` directory contains the Angular client application.

The `backend` directory contains the Spring Boot REST API and business logic.

The `docs` directory contains project documentation and technical diagrams.

## Getting Started
### Prerequisites

The following software is required to run the project locally:

Java 21
Maven
Node.js
npm
Angular CLI
MongoDB

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
ng serve
```

The application will then be available through the local development
environment.

## Documentation

Additional technical documentation, architecture diagrams and design decisions
will be progressively added to the `docs` directory as the project evolves.

## License

This project is licensed under the MIT License.
See the `LICENSE` file for more information.
