package cz.fi.muni.pv217.dlp.SmtpServer;

import cz.fi.muni.pv217.dlp.SmtpServer.Message.EmailMessage;
import cz.fi.muni.pv217.dlp.SmtpServer.Message.SmtpServerException;

public interface SmtpServer {

    void sendMessage(EmailMessage emailMessage) throws SmtpServerException;
}
