package cz.fi.muni.pv217.dlp.SmtpServer.Message;

import javax.mail.Message;

public class Recipient {

    private Message.RecipientType type;
    private String address;

    public Recipient(Message.RecipientType type, String address) {
        this.type = type;
        this.address = address;
    }

    public Message.RecipientType getType() {
        return type;
    }

    public void setType(Message.RecipientType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
