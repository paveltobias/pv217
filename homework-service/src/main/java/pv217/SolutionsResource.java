package pv217;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Path("/solutions")
public class SolutionsResource {

    private static final Logger LOG = Logger.getLogger(SolutionsResource.class);

    @ConfigProperty(name = "pv217.courseServiceBaseUrl")
    String courseSvcBaseUrl;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"teacher", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Solution> getSolutions() {
        Set<String> groups = jwt.getGroups();
        if (groups.contains("teacher")) {
            // TODO: return all solutions for all assignments which they have published
        }
        if (groups.contains("student")) {
            // TODO: return all solutions submitted by the student
        }
        return List.of();
    }

    @POST
    @RolesAllowed({"student"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postSolution(Solution solution) {
        try {
            solution.persist();
        } catch (ConstraintViolationException ex) {
            LOG.error("Failed to persist solution", ex);
            return Response.status(404).build();
        }

        return Response.ok(solution).build();
    }

    @PATCH
    @Path("{solution_id}")
    @RolesAllowed({"teacher"})
    @Consumes(MediaType.APPLICATION_JSON) // FIXME: maybe there is something better for enum...
    @Produces(MediaType.APPLICATION_JSON)
    public Response markSolution(Mark mark, @PathParam("solution_id") Long solutionId) {



        Solution solution = Solution.findById(solutionId);

        if (solution == null) {
            LOG.error("Failed to find solution with id " + solutionId);
            return Response.status(404).build();
        }


        // TODO: check if related Assignment is owned by teacher!


        solution.mark = mark;
        solution.persist();

        return Response.ok().build();
    }

}
