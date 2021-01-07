package cz.fi.muni.pv217.dlp.SmtpServer.Message;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public class Util {

    public static MimeMessage toMime(Session session, EmailMessage emailMessage) throws SmtpServerException {
        MimeMessage msg = new MimeMessage(session);

        try {
            msg.setFrom(emailMessage.getFrom());
        } catch (MessagingException e) {
            throw new SmtpServerException("Invalid sender's email address", e);
        }

        try {
            for (Recipient recipient : emailMessage.getRecipients()) {
                msg.addRecipient(recipient.getType(), new InternetAddress(recipient.getAddress()));
            }
        } catch (MessagingException e) {
            throw new SmtpServerException("Invalid receiver's email address", e);
        }

        try {
            msg.setSubject(emailMessage.getSubject());
        } catch (MessagingException e) {
            throw new SmtpServerException("Invalid email subject", e);
        }

        try {
            msg.setSentDate(new Date());
        } catch (MessagingException e) {
            throw new SmtpServerException("Invalid sent date", e);
        }

        try {
            msg.setText(emailMessage.getBody());
        } catch (MessagingException e) {
            throw new SmtpServerException("Invalid email body", e);
        }

        return msg;
    }

}
