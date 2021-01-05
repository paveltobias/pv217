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
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;
import pv217.entities.Assignment;
import pv217.entities.extern.Course;

@Path("/assignments")
public class AssignmentsResource {

    private static final Logger LOG = Logger.getLogger(SolutionsResource.class);

    @ConfigProperty(name = "pv217.courseServiceBaseUrl")
    String courseSvcBaseUrl;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"teacher", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "getAssignments", description = "How many times assignments were returned.")
    @Timed(name = "getAssignmentsTimer", description = "How long it takes to return assignments.")
    public List<Assignment> getAssignments() {
        Long uid = Long.decode(jwt.getName());

        Set<String> groups = jwt.getGroups();
        if (groups.contains("teacher")) {
            LOG.info("Obtaining assignments for teacher id " + uid);
            return Assignment.listByTeacher(uid);
        }
        if (groups.contains("student")) {
            LOG.info("Obtaining assignments for student id " + uid);
            return Assignment.listByCourses(getReggedCourses());
        }
        return List.of();
    }

    @POST
    @RolesAllowed("teacher")
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "postAssignment", description = "How many assignments were posted.")
    @Timed(name = "postAssignmentTimer", description = "How long it takes to post an assignment.")
    public Response postAssignment(Assignment ass) {
        LOG.info("Posting assignment " + ass.toString());

        if (ass.description == null ||
            ass.courseId == null ||
            !courseExists(ass.courseId)) {
            LOG.error("Trying to post invalid assignment");
            return Response.status(404).build();
        }
        ass.teacherId = Long.decode(jwt.getName());
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