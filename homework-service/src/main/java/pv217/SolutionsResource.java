package pv217;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
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

    @ConfigProperty(name = "pv217.userServiceBaseUrl")
    String userSvcBaseUrl;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"teacher", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Solution> getSolutions() {
        Set<String> groups = jwt.getGroups();
        if (groups.contains("teacher")) {
            // returns all solutions to all assignments that given teacher has published
            return Assignment.listByTeacher(Long.decode(jwt.getSubject())).stream()
                    .flatMap(assignment -> assignment.solution.stream()).collect(Collectors.toList());
        }
        if (groups.contains("student")) {
            // returns all solutions of given student (for any assignments)
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

    @Inject
    @Channel("marks")
    Emitter<MarkDTO> marksEmitter;

    @PATCH
    @Path("{solution_id}")
    @RolesAllowed("teacher")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response markSolution(MarkJson markJson, @PathParam("solution_id") Long solutionId) {
        // get solution
        Solution solution = Solution.findById(solutionId);
        if (solution == null) {
            LOG.error("Failed to find solution with id " + solutionId);
            return Response.status(404).build();
        }

        // check whether related Assignment is owned by this teacher
        Long teacherId = Long.decode(jwt.getSubject());
        if (!solution.assignment.teacherId.equals(teacherId)) {
            LOG.warn("Teacher id " + teacherId + " tried to mark solution of assignment " +
                    "that (s)he didn't publish");
            Response.status(403).build();
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
}
