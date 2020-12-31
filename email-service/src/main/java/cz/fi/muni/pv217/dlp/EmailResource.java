package cz.fi.muni.pv217.dlp;

import cz.fi.muni.pv217.dlp.SmtpServer.Message.EmailMessage;
import cz.fi.muni.pv217.dlp.SmtpServer.Message.SmtpServerException;
import cz.fi.muni.pv217.dlp.SmtpServer.SmtpServer;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.mail.Message;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

// TODO: Remove/replace this resource.


@Path("/email")
public class EmailResource {

    private static final Logger LOG = Logger.getLogger(EmailResource.class);

    @Inject
    SmtpServer smtpServer;

    @POST
    @Path("/send")
    @RolesAllowed({"teacher"})
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendEmail(EmailMessage emailMessage) throws SmtpServerException {
        smtpServer.sendMessage(emailMessage);
    }

    @GET
    @Path("/test")
    public String test() {
        EmailMessage emailMessage = new EmailMessage.Builder().setFrom("test_teacher@gmail.com")
                .addRecipient(Message.RecipientType.TO, "test_student@mail.muni.cz")
                .setSubject("test").setBody("hello").build();
        try {
            smtpServer.sendMessage(emailMessage);
        } catch (SmtpServerException e) {
            e.printStackTrace();
        }

        return "test message sent";
    }




    //@Inject
    //@Channel("marking")
    //Emmiter<> // marks {student, assignment, how much points}
}
