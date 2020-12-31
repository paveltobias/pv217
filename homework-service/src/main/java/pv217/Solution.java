package pv217;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Entity
public class Solution extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String content;

    @ManyToOne
    @JoinColumn(name = "assignment_id", unique = true)
    @NotNull
    public Assignment assignment;

    @Enumerated
    @NotNull
    public Mark mark;

    //@NotNull
    //@Temporal(TemporalType.DATE)
    //public Date submissionDate;

    //public Solution {
    //}






}
