package pv217.entities.extern;


import pv217.entities.Solution;

public class SolutionDTO {
    public Long id;
    public String content;
    public static SolutionDTO create(Solution solution) {
        SolutionDTO solutionDTO = new SolutionDTO();
        solutionDTO.content= solution.content;
        solutionDTO.id = solution.id;
        return solutionDTO;
    }

}
