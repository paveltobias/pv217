package cz.fi.muni.pv217.dlp.SmtpServer;

import com.dumbster.smtp.SimpleSmtpServer;
import cz.fi.muni.pv217.dlp.SmtpServer.Message.EmailMessage;
import cz.fi.muni.pv217.dlp.SmtpServer.Message.SmtpServerException;
import cz.fi.muni.pv217.dlp.SmtpServer.Message.Util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

public class MockSmtpServerImpl implements SmtpServer {

    private SimpleSmtpServer dumbster;
    private Session session;

    public MockSmtpServerImpl() {
        try {
            dumbster = SimpleSmtpServer.start(SimpleSmtpServer.AUTO_SMTP_PORT);
        } catch (IOException e) {
            throw new RuntimeException("Unable to start mock smtp server", e);
        }

        Properties sessionProperties = new Properties();
        sessionProperties.setProperty("mail.smtp.host", "localhost");
        sessionProperties.setProperty("mail.smtp.port", "" + dumbster.getPort());
        sessionProperties.setProperty("mail.smtp.sendpartial", "true");

        session = Session.getInstance(sessionProperties, null);
    }

    @Override
    public void sendMessage(EmailMessage emailMessage) throws SmtpServerException {
        MimeMessage mimeMessage = Util.toMime(session, emailMessage);

        try {
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            throw new SmtpServerException("Failed to send message", e);
        }
    }
}
