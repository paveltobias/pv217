package pv217;

import java.util.List;
import java.util.stream.Stream;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Course extends PanacheEntity {
    public static Stream<Course> streamRegistered(Long studentId) {
        Stream<Course> stream = streamAll();
        return stream.filter(c -> c.studentIds.contains(studentId));
    }

    public String name;

    @ElementCollection
    public List<Long> studentIds;
}
