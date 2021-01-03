package pv217.entities;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import pv217.entities.extern.Course;

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Assignment extends PanacheEntityBase {
    public static List<Assignment> listByCourses(Collection<Course> courses) {
        Set<Long> courseIds = courses
            .stream()
            .map(c -> c.id)
            .collect(Collectors.toSet());
        Stream<Assignment> stream = streamAll();
        return stream
            .filter(a -> courseIds.contains(a.id))
            .collect(Collectors.toList());
    }

    public static List<Assignment> listByTeacher(Long teacherId) {
        return Assignment.list("teacherId", teacherId);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long teacherId;
    public Long courseId;
    public String description;

    @OneToMany(targetEntity = Solution.class, mappedBy = "assignment", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Solution> solution;

    //@NotNull
    //@Temporal(TemporalType.DATE)
    //public Date deadline;
}
