package cz.fi.muni.pv217.dlp.SmtpServer.Message;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EmailMessage {

    private String from;
    private Collection<Recipient> recipients;
    private String subject;
    private String body;

    public String getFrom() {
        return from;
    }

    public Collection<Recipient> getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    private EmailMessage(String from, Collection<Recipient> recipients, String subject, String body) {
        this.from = from;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
    }

    public static class Builder {
        private String from;
        private List<Recipient> recipients = new ArrayList<>();
        private String subject;
        private String body;

        public Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder addRecipient(Message.RecipientType recipientType, String recipient) {
            this.recipients.add(new Recipient(recipientType, recipient));

            return this;
        }

        public Builder addRecipients(Collection<Recipient> recipients) {
            this.recipients.addAll(recipients);
            return this;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public EmailMessage build() {
            return new EmailMessage(from, recipients, subject, body);
        }
    }

}
