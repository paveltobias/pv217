package cz.fi.muni.pv217.dlp.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    //public Long id;
    public String name;
    public String email;
    //public String role;
}
