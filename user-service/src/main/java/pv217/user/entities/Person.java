package pv217.user.entities;

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

    public String email;
    public String password;
    public String name;
    public Role role;
}
