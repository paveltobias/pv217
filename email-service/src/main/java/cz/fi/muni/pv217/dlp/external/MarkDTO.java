package cz.fi.muni.pv217.dlp.external;

public class MarkDTO {
    public String email;
    public String name;
    public String assignment;
    public Mark mark;

    @Override
    public String toString() {
        return "MarkDTO{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", assignment='" + assignment + '\'' +
                ", mark=" + mark +
                '}';
    }
}

