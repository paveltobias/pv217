package pv217.entities.extern;

import pv217.entities.Mark;
import pv217.entities.Solution;

public class MarkDTO {
    public String email;
    public String name;
    public String assignment;
    public Mark mark;

    public static MarkDTO create(Solution solution, User student) {
        MarkDTO markDTO = new MarkDTO();
        markDTO.email = student.email;
        markDTO.name = student.name;
        markDTO.assignment = solution.assignment.description;
        markDTO.mark = solution.mark;
        return markDTO;
    }
}
