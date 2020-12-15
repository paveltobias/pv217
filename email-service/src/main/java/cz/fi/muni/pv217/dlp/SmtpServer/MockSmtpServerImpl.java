package cz.fi.muni.pv217.dlp.SmtpServer;

import com.dumbster.smtp.SimpleSmtpServer;
import cz.fi.muni.pv217.dlp.SmtpServer.Message.EmailMessage;
import cz.fi.muni.pv217.dlp.SmtpServer.Message.SmtpServerException;
import cz.fi.muni.pv217.dlp.SmtpServer.Message.Util;
import org.jboss.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Properties;

public class MockSmtpServerImpl implements SmtpServer {

    private SimpleSmtpServer dumbster;
    private Session session;

    private static final Logger LOG = Logger.getLogger(MockSmtpServerImpl.class);

    public MockSmtpServerImpl() {
        LOG.info("Creating new mock smtp server");

        try {
            dumbster = SimpleSmtpServer.start(SimpleSmtpServer.AUTO_SMTP_PORT);
        } catch (IOException e) {
            throw new RuntimeException("Unable to start mock smtp server", e);
        }

        LOG.debug("Mock smtp server created, setting properties");

        Properties sessionProperties = new Properties();
        sessionProperties.setProperty("mail.smtp.host", "localhost");
        sessionProperties.setProperty("mail.smtp.port", "" + dumbster.getPort());
        sessionProperties.setProperty("mail.smtp.sendpartial", "true");

        session = Session.getInstance(sessionProperties, null);

        LOG.debug("Created smtp session");
    }

    @Override
    public void sendMessage(EmailMessage emailMessage) throws SmtpServerException {
        LOG.debug("Sending message " + emailMessage.toString());

        MimeMessage mimeMessage = Util.toMime(session, emailMessage);

        try {
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            throw new SmtpServerException("Failed to send message", e);
        }
    }
}
