package pv217;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long courseId;
    public String description;
}
