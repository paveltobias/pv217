package pv217;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/assignments")
public class AssignmentsResource {
    @ConfigProperty(name = "pv217.courseServiceBaseUrl")
    String courseSvcBaseUrl;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"teacher", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Assignment> getAssignments() {
        Set<String> groups = jwt.getGroups();
        if (groups.contains("teacher")) {
            return Assignment.listAll();
        }
        if (groups.contains("student")) {
            return Assignment.listByCourses(getReggedCourses());
        }
        return List.of();
    }

    @POST
    @RolesAllowed("teacher")
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postAssignment(Assignment ass) {
        if (ass.description == null ||
            ass.courseId == null ||
            !courseExists(ass.courseId)) {
            return Response.status(404).build();
        }
        ass.persist();
        return Response.ok(ass).build();
    }

    List<Course> getReggedCourses() {
        return ClientBuilder.newClient()
            .target(courseSvcBaseUrl + "/courses")
            .request()
            .header("Authorization", "Bearer " + jwt.getRawToken())
            .get()
            .readEntity(new GenericType<List<Course>>(){});
    }

    boolean courseExists(Long courseId) {
        int status = ClientBuilder.newClient()
            .target(courseSvcBaseUrl + "/courses/" + courseId)
            .request()
            .header("Authorization", "Bearer " + jwt.getRawToken())
            .get()
            .getStatus();
        return status == 200;
    }
}