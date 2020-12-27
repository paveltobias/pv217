package pv217;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/courses")
public class CoursesResource {
    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"teacher", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getCourses() {
        Stream<Course> stream = jwt.getGroups().contains("teacher")
            ? Course.streamAll()
            : Course.streamRegistered(Long.decode(jwt.getSubject()));
        return stream
            .map(c -> sanitizeCourse(c))
            .collect(Collectors.toList());
    }

    @PATCH
    @Path("{id}")
    @RolesAllowed("teacher")
    @Transactional(TxType.REQUIRES_NEW)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchCourse(
        @PathParam("id") Long courseId,
        Course patch
    ) {
        Course course = Course.findById(courseId);
        if (course == null) {
            return Response.status(404).build();
        }
        if (patch.name != null) {
            course.name = patch.name;
        }
        if (patch.studentIds != null) {
            course.studentIds = patch.studentIds;
        }
        course.persist();
        return Response.ok(sanitizeCourse(course)).build();
    }

    Course sanitizeCourse(Course course) {
        Course sanCourse = new Course();
        sanCourse.id = course.id;
        sanCourse.name = course.name;
        sanCourse.studentIds = jwt.getGroups().contains("teacher")
            ? course.studentIds
            : List.of(Long.decode(jwt.getSubject()));
        return sanCourse;
    }
}
