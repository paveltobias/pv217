package pv217.user.resources;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.Claim;

import pv217.user.entities.Person;
import pv217.user.models.User;

@Path("/users")
public class UsersResource {
    @Claim("groups")
    Set<String> groups;

    @Claim("userId")
    Long userId;

    @GET
    @RolesAllowed("teacher")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() {
        List<Person> persons = Person.listAll();
        return persons
            .stream()
            .map(p -> buildUser(p))
            .collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"teacher", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        if (userId.compareTo(id) != 0 &&
            !groups.contains("teacher")) {
            return Response.status(403).build();
        }
        Person person = Person.findById(id);
        if (person == null) {
            return Response.status(404).build();
        }
        return Response.ok(buildUser(person)).build();
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
