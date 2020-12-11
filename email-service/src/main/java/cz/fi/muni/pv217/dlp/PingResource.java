package cz.fi.muni.pv217.dlp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ping")
public class PingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "Pong 1";
    }

    @GET
    @Path("/call-another")
    public String CallAnother() {
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://localhost:8081")
                                  .path("ping")
                                  .request()
                                  .get();
        System.out.println(response.getStatus());
        String s = response.readEntity(String.class);
        System.out.println(s);
        return s;
    }
}