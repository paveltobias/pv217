package cz.fi.muni.pv217.dlp.SmtpServer;

import cz.fi.muni.pv217.dlp.SmtpServer.Message.EmailMessage;
import cz.fi.muni.pv217.dlp.SmtpServer.Message.SmtpServerException;

import javax.mail.MessagingException;

public interface SmtpServer {

    void sendMessage(EmailMessage emailMessage) throws SmtpServerException;
}
