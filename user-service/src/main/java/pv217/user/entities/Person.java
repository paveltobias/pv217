package pv217.user.entities;

import java.util.Collection;
import java.util.stream.Stream;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Person extends PanacheEntity {
    public static Person findById(Long id) {
        return find("id", id).firstResult();
    }

    public static Person findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public static Stream<Person> streamByIds(Collection<Long> ids) {
        Stream<Person> stream = streamAll();
        return stream.filter(p -> ids.contains(p.id));
    }

    public String email;
    public String password;
    public String name;
    public Role role;
}
