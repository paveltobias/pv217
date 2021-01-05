package pv217;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;
import pv217.entities.Assignment;
import pv217.entities.Mark;
import pv217.entities.Solution;
import pv217.entities.extern.MarkDTO;
import pv217.entities.extern.MarkJson;
import pv217.entities.extern.User;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import static pv217.entities.Solution.fetchStudentsSolutions;

@Path("/solutions")
public class SolutionsResource {

    private static final Logger LOG = Logger.getLogger(SolutionsResource.class);

    // maximum number of solutions returned in one call
    private long maxSolutionsReturned = 0;

    @ConfigProperty(name = "pv217.userServiceBaseUrl")
    String userSvcBaseUrl;

    @Inject
    JsonWebToken jwt;

    @Inject
    @Channel("marks")
    Emitter<MarkDTO> marksEmitter;

    /**
     * If called by a teacher, returns all solutions for
     * all assignments that were created by him.
     * If called by a student, returns all of his solutions.
     */
    @GET
    @RolesAllowed({"teacher", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "getSolutions", description = "How many times solutions were obtained.")
    @Timed(name = "getSolutionsTimer", description = "How long it takes to obtain solutions.")
    public List<Solution> getSolutions() {
        Long uid = Long.decode(jwt.getSubject());
        Set<String> groups = jwt.getGroups();

        List<Solution> solutions = null;
        if (groups.contains("teacher")) {
            LOG.info("Fetching solutions to assignments created by teacher id " + uid);
            solutions = Assignment.listByTeacher(Long.decode(jwt.getSubject())).stream()
                    .flatMap(assignment -> assignment.solution.stream()).collect(Collectors.toList());
        }
        else if (groups.contains("student")) {
            LOG.info("Fetching solutions published by student id " + uid);
            solutions = fetchStudentsSolutions(Long.decode(jwt.getSubject()));
        }
        else {
            assert false;
            return List.of();
        }

        if (solutions.size() > maxSolutionsReturned) {
            maxSolutionsReturned = solutions.size();
        }

        return solutions;
    }

    /**
     * Adds solution to a database.
     *
     * Note: student may publish a solution to an assignment
     * for a course that (s)he does not have. We may implement
     * check for this later (it would require calling course service).
     *
     * TODO: call plagiarism detection service (kafka channel)
     */
    @POST
    @RolesAllowed("student")
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "postSolution", description = "How many solutions were posted.")
    @Timed(name = "postSolutionTimer", description = "How long it takes to post a solution.")
    public Response postSolution(Solution solution) {
        LOG.info("Posting solution: " + solution);

        // do not allow student to publish marked solution
        solution.mark = Mark.NA;
        // do not allow student to publish solution as someone else
        solution.studentId = Long.decode(jwt.getSubject());

        if (Assignment.findById(solution.assignmentId) == null) {
            LOG.error("Failed to persist solution because no assignment with id {"
                    + solution.assignmentId + "} exists");
            return Response.status(400).build();
        }

        try {
            solution.persist();
        } catch (ConstraintViolationException ex) {
            LOG.error("Failed to persist solution", ex);
            return Response.status(400).build();
        }

        LOG.info("Successfully published solution id " + solution.id);
        return Response.ok(solution).build();
    }

    /**
     * Marks a solution. Only teacher that posted
     * an assignment can mark its solutions.
     *
     * Mark can be changed, but not to Mark.NA (no mark).
     */
    @PATCH
    @Path("{solution_id}")
    @RolesAllowed("teacher")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "markSolution", description = "How many solutions were marked.")
    @Timed(name = "markSolutionTimer", description = "How long it takes to mark a solution.")
    public Response markSolution(MarkJson markJson, @PathParam("solution_id") Long solutionId) {
        // get solution
        Solution solution = Solution.findById(solutionId);
        if (solution == null) {
            LOG.error("Failed to find solution with id " + solutionId);
            return Response.status(404).build();
        }

        Long teacherId = Long.decode(jwt.getSubject());
        if (markJson.mark == Mark.NA) {
            LOG.warn("Teacher id " + teacherId + " tried to change mark of a solution to NA (remove mark).");
            return Response.status(400).build();
        }

        // check whether related Assignment is owned by this teacher
        if (!solution.assignment.teacherId.equals(teacherId)) {
            LOG.warn("Teacher id " + teacherId + " tried to mark solution of assignment " +
                    "that (s)he didn't publish");
            return Response.status(403).build();
        }

        solution.mark = markJson.mark;

        // create data transfer object. This is expensive, because it
        // needs to find user (contact other service). We could delegate
        // finding user to EmailSevice but we would have to send token too (I think)
        MarkDTO markDTO = MarkDTO.create(solution, obtainUser(solution.studentId));

        // inform student (fire and forget)
        marksEmitter.send(markDTO);

        solution.persist();
        return Response.ok(solution).build();
    }

    private User obtainUser(Long userID) {
        assert userID != null; // caller should guarantee this

        LOG.info("Obtaining user id " + userID.toString());

        URIBuilder uri;
        try {
            uri = new URIBuilder(userSvcBaseUrl);
        } catch (URISyntaxException e) {
            LOG.error("Invalid base url: " + userSvcBaseUrl);
            return null;
        }

        uri.setPath("users/" + userID.toString());
        return ClientBuilder.newClient()
                .target(uri.toString())
                .request()
                .header("Authorization", "Bearer " + jwt.getRawToken())
                .get()
                .readEntity(new GenericType<User>(){});
    }

    @Gauge(name = "maxSolutionsReturned", unit = MetricUnits.NONE,
            description = "Maximum number of solutions returned in one GET call.")
    public Long maxSolutionsReturned() {
        return maxSolutionsReturned;
    }
}
