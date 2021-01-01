package pv217;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "solutions")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Solution extends PanacheEntityBase {

    public static List<Solution> fetchStudentsSolutions(Long studentId) {
        return Solution.find("studentId", studentId).list();
    }

    //public static List<Solution> fetchTeachersSolutions(Long teacherId) {
    //    return Solution.find("studentId", studentId).list();
    //}


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String content;

    @NotNull(message = "Assignment is not set")
    @ManyToOne
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    @JsonIgnore // json will contain only assignment id
    public Assignment assignment;

    @NotNull(message = "Assignment id is not set")
    @Column(name = "assignment_id")
    public Long assignmentId;

    public Long studentId;

    @Enumerated
    @NotNull
    public Mark mark;

    //@NotNull
    //@Temporal(TemporalType.DATE)
    //public Date submissionDate;

    //public Solution {
    //}


    @Override
    public String toString() {
        return "Solution{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", assignment=" + assignment +
                ", assignmentId=" + assignmentId +
                ", mark=" + mark +
                '}';
    }
}
