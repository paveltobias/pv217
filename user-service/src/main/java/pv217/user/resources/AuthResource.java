package pv217.user.resources;

import java.util.HashSet;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.smallrye.jwt.build.Jwt;
import pv217.user.entities.Person;
import pv217.user.models.LoginInfo;

@Path("/auth")
public class AuthResource {
    @POST
    @Path("login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String logIn(LoginInfo info) {
        Person person = Person.findByEmail(info.email);
        if (person == null) {
            throw new WebApplicationException(404);
        }
        if (!info.pass.equals(person.password)) {
            throw new WebApplicationException(401);
        }
        HashSet<String> groups = new HashSet<String>(
            List.of(person.role.toString())
        );
        String jwt = Jwt.groups(groups).subject(person.id.toString()).sign();
        return jwt;
    }
}
