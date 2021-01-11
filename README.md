# PV217 -- Distance Learning Platform

For now, a rough spec draft/discussion is available [here](https://github.com/paveltobias/pv217/wiki/Project-Domain-Discussion).

## Authors

- Svorad Simko &lt;433512@mail.muni.cz&gt;
- Jakub Dóczy &lt;423199@mail.muni.cz&gt;
- Pavel Tobiáš &lt;422611@mail.muni.cz&gt;

## Dependencies
- JDK 11
- Maven 3.6.2+
- Docker
- docker-compose
- HTTPie (optional; for test suite)

## Building &amp; Running

### Via `docker-compose`

1. Run `./build.sh` to clean-install the services' dependencies, build the services and package them into JAR files.
2. Run `docker-compose build` to build Docker images for all of the services.
3. Run `docker-compose up` to run the services, PostgreSQL and Kafka in their respective containers.

The services should be listening on `http://localhost` on the following ports:

- `8081` (user service);
- `8082` (course service);
- `8083` (homework service);
- `8089` (email service).

### For Development

1. Run PostgreSQL. Make sure it is listening on `localhost:5432` and has a database called `d` accessible with the username `u` and password `p`.
2. Setup and run Kafka, e.g. according to [this](https://github.com/xstefank/pv217-microservices/tree/master/05-additional/kafka-quickstart) example.
3. Run `mvn quarkus:dev` inside each of `user-service`, `course-service`, `homework-service` and `email-service` directories concurrently.

The services should be listening on the same ports as when run via `docker-compose`.

## Testing

An end-to-end test suite is prepared in `./test.sh`. The script requires [HTTPie](https://httpie.io). If uncertain about the echoed HTTP calls' semantics, check out the contents of the script -- it is thoroughly commented.

## Documentation

A dynamically generated [OpenAPI](https://swagger.io/specification) specification is available for each of the services:

- as a human-readable web UI at the `/swagger-ui` endpoint;
- in raw YAML format at the `/openapi` endpoint.

The latest snapshots of the specs are also available in the [openapi](./openapi) directory of this repository.

### User Service

This service stores users -- teachers and students -- in a relational database. It facilitates:

- authentication/authorization of users;
- listing users.

It has the following endpoints:

- `POST /auth/login` -- Consumes a JSON object with `email` and `pass` properties. If an email-password pair from the database is matched, a time-limited JWT token is returned as a `text/plain` response. The token encodes the role (student vs. teacher) and the ID of the logged-in user; it is meant to be used for [bearer authentication](https://swagger.io/docs/specification/authentication/bearer-authentication/) when calling the remaining endpoints of this service, as well as endpoints of other services.
- `GET /users` -- Lists all users. Can only be successfully called by a teacher (otherwise a `403` response is returned).
- `GET /users/{id}` -- Returns a single user with a given `{id}`. A student can only retrieve themselves (otherwise a `403` response is returned). A teacher can successfully retrieve anyone.

### Homework Service

This service stores assignments and solutions (of assignments).

It has the following endpoints:

- `GET /assignments` -- If called by a teacher, returns all assignments created by him. If called by a student, returns all assignments from all registered courses.
- `POST /assignments` -- Creates new assignment. Can only be called by a teacher.
- `GET /solutions` -- If called by a teacher, returns all solutions for all assignments that were created by the teacher. If called by a student, returns all of his/her solutions.
- `POST /solutions` -- Stores a solution. Can only be called by a student.
- `PATCH /solution/{id}` -- Marks a solution. Informs student that posted given solution via email-service (reactive). Can only be called by a teacher that created related assignment.

### Email Service

Informs students when their solution gets marked.
