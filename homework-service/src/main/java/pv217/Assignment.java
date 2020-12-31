package pv217;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.*;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Assignment extends PanacheEntityBase {
    static List<Assignment> listByCourses(Collection<Course> courses) {
        Set<Long> courseIds = courses
            .stream()
            .map(c -> c.id)
            .collect(Collectors.toSet());
        Stream<Assignment> stream = streamAll();
        return stream
            .filter(a -> courseIds.contains(a.id))
            .collect(Collectors.toList());
    }

    /**
     * Returns all solutions related to the assignment.
     */
    public List<Solution> getSolution() {
        return Solution.list("assignment_id", id);
    }

    /**
     * Returns solution of given student.
     */
    public List<Solution> getStudentsSolution(Long studentId) {
        return Solution.find("assignment_id = ?1 and student_id = ?2", id, studentId).firstResult();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long courseId;
    public String description;

    //@NotNull
    //@Temporal(TemporalType.DATE)
    //public Date deadline;
}
