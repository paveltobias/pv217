package pv217.user.resources;

import java.util.HashSet;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    public Response logIn(LoginInfo info) {
        Person person = Person.findByEmail(info.email);
        if (person == null) {
            return Response.status(404).build();
        }
        if (!info.pass.equals(person.password)) {
            return Response.status(401).build();
        }
        HashSet<String> groups = new HashSet<>();
        groups.add(person.role.toString());
        String jwt = Jwt.groups(groups).upn(person.id.toString()).sign();
        return Response.ok(jwt).build();
    }
}
