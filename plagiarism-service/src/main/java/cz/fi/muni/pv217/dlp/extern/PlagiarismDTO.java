package cz.fi.muni.pv217.dlp.extern;

public class PlagiarismDTO {
    public Long id;
    public static PlagiarismDTO create(Long id) {
        PlagiarismDTO plag = new PlagiarismDTO();
        plag.id = id;
        return plag;
    }
}
