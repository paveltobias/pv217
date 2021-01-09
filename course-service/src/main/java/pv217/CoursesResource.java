package pv217;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

@Path("/courses")
public class CoursesResource {
    static final Logger LOG = Logger.getLogger(CoursesResource.class);

    @ConfigProperty(name = "pv217.userServiceBaseUrl")
    String userSvcBaseUrl;

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

    @GET
    @Path("{id}")
    @RolesAllowed("teacher")
    @Produces(MediaType.APPLICATION_JSON)
    public Course getCourse(@PathParam("id") Long id) {
        Course course = Course.findById(id);
        if (course == null) {
            throw new WebApplicationException(404);
        }
        return course;
    }

    @PATCH
    @Path("{id}")
    @RolesAllowed("teacher")
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Course patchCourse(
        @PathParam("id") Long courseId,
        Course patch
    ) {
        Course course = Course.findById(courseId);
        if (course == null) {
            throw new WebApplicationException(404);
        }
        if (patch.name != null) {
            course.name = patch.name;
        }
        if (patch.studentIds != null) {
            if (!areStudentIds(patch.studentIds)) {
                throw new WebApplicationException(400);
            }
            course.studentIds = patch.studentIds;
        }
        course.persist();
        return sanitizeCourse(course);
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

    boolean areStudentIds(List<Long> studentIds) {
        URIBuilder uri;
        try {
            uri = new URIBuilder(userSvcBaseUrl);
        } catch (URISyntaxException e) {
            LOG.error("Invalid base url: " + userSvcBaseUrl);
            return false;
        }
        uri.setPath("users").setParameter("id", Utils.join(studentIds));
        List<User> res = ClientBuilder.newClient()
            .target(uri.toString())
            .request()
            .header("Authorization", "Bearer " + jwt.getRawToken())
            .get()
            .readEntity(new GenericType<List<User>>(){});
        return (
            res.size() == studentIds.size() &&
            res.stream().allMatch(u -> u.role.equals("student"))
        );
    }
}
