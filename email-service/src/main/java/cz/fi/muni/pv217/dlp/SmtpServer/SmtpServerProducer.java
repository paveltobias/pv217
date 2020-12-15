package cz.fi.muni.pv217.dlp.SmtpServer;

import javax.enterprise.inject.Produces;

public class SmtpServerProducer {

    @Produces
    public SmtpServer mockSmtpServerProducer() {
        return new MockSmtpServerImpl();
    }

}
