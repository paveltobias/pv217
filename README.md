# PV217 -- Distance Learning Platform

For now, a rough spec draft/discussion is available [here](https://github.com/paveltobias/pv217/wiki/Project-Domain-Discussion).

## Dependencies
- JDK 11
- Maven 3.6.2+
- Docker
- docker-compose
- PostgreSQL
- HTTPie

## Documentation

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


## OpenAPI

The aut-generated OpenAPI spec is available at the following endpoints:

- `GET /openapi` (as `application/yaml`);
- `GET /swagger-ui` (graphical representation).
## Running in docker-compose
Create jars for every service with
`./mvnw package`
Then you can run everything with 
`docker-compose up --build`
