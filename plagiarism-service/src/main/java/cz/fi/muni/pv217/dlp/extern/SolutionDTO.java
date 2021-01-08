package cz.fi.muni.pv217.dlp.extern;


public class SolutionDTO {
    public Long id;
    public String content;

    @Override
    public String toString() {
        return "SolutionDTO{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
