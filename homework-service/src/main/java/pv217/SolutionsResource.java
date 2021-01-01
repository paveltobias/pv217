package pv217;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

import static pv217.Solution.fetchStudentsSolutions;

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
            return fetchStudentsSolutions(Long.decode(jwt.getSubject()));
        }
        return List.of();
    }

    @POST
    @RolesAllowed("student")
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postSolution(Solution solution) {
        // do not allow student to publish marked solution
        solution.mark = Mark.NA;
        // do not allow student to publish solution as someone else
        solution.studentId = Long.decode(jwt.getSubject());

        if (Assignment.findById(solution.assignmentId) == null) {
            LOG.error("Failed to persist solution because no assignment with id {"
                    + solution.assignmentId + "} exists");
            return Response.status(404).build();
        }

        try {
            solution.persist();
        } catch (ConstraintViolationException ex) {
            LOG.error("Failed to persist solution", ex);
            return Response.status(404).build();
        }

        LOG.info("Successfully published solution id " + solution.id);
        return Response.ok(solution).build();
    }

    @PATCH
    @Path("{solution_id}")
    @RolesAllowed("teacher")
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
