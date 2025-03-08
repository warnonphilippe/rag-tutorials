package com.github.rag.tutorials.helpdesk.infrastructure.config.camel;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Properties;

@Slf4j
public class ImapEmailTest {
    @Test
    public void testSendAndReceiveMail() throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "false");
        properties.put("mail.smtp.starttls.enable", "false");
        properties.put("mail.smtp.host", "localhost");
        properties.put("mail.smtp.port", "3025");

        final String username = System.getenv("MAIL_SMTP_USERNAME");
        final String password = System.getenv("MAIL_SMTP_PASSWORD");

        // Creazione della sessione con autenticazione
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Creazione del messaggio
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username));
            message.setSubject("Test Email da Java");
            message.setText("Ciao,\n\nQuesto è un test di invio email da Java.\n\nSaluti,\nIl tuo programma Java");

            // Invio dell'email
            Transport.send(message);

            System.out.println("Email inviata con successo!");

        } catch (MessagingException e) {
            log.error("Errore durante l'invio dell'email", e);
        }
    }
}