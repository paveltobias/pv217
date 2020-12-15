package cz.fi.muni.pv217.dlp.SmtpServer.Message;

public class SmtpServerException extends Exception {

    public SmtpServerException(String message) {
        super(message);
    }

    public SmtpServerException(String message, Exception e) {
        super(message, e);
    }

}
