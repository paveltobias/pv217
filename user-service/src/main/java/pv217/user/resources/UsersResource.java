package pv217.user.resources;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;

import pv217.user.entities.Person;
import pv217.user.models.User;

@Path("/users")
public class UsersResource {
    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed("teacher")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers(@QueryParam("id") String id) {
        Stream<Person> stream;
        if (id != null) {
            try {
                Set<Long> ids = Arrays
                    .stream(id.split(","))
                    .map(i -> Long.decode(i))
                    .collect(Collectors.toSet());
                stream = Person.streamByIds(ids);
            } catch (NumberFormatException e) {
                throw new WebApplicationException(404);
            }
        } else {
            stream = Person.streamAll();
        }
        List<User> users = stream
            .map(p -> buildUser(p))
            .collect(Collectors.toList());
        return users;
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"teacher", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserById(@PathParam("id") Long id) {
        if (Long.decode(jwt.getSubject()).equals(id) &&
            !jwt.getGroups().contains("teacher")) {
                throw new WebApplicationException(403);
        }
        Person person = Person.findById(id);
        if (person == null) {
            throw new WebApplicationException(404);
        }
        return buildUser(person);
    }

    User buildUser(Person person) {
        User user = new User();
        user.id = person.id;
        user.name = person.name;
        user.email = person.email;
        user.role = person.role.toString();
        return user;
    };
}
