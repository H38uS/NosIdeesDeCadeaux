package com.mosioj.ideescadeaux.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EmailSender {

    private static final Logger logger = LogManager.getLogger(EmailSender.class);

    // Properties
    public static final Properties MY_PROPERTIES = new Properties();
    private static final Properties SYSTEM_PROP = System.getProperties();

    /** Envoie de 4 mails en parrallèle */
    private static final ExecutorService executor = Executors.newFixedThreadPool(8);

    /**
     * Sends out an email.
     *
     * @param to       The email address where to send the email.
     * @param subject  The email subject.
     * @param htmlText The email body, html formated.
     */
    public static Future<?> sendEmail(String to, String subject, String htmlText) {
        if (MY_PROPERTIES.getProperty("host").isBlank()) {
            logger.info("No host info => do not send the email.");
            return CompletableFuture.completedFuture("Nothing to do!");
        }
        return executor.submit(() -> {
            try {
                logger.info(MessageFormat.format("Sending email to {0}...", to));
                Session session = Session.getDefaultInstance(SYSTEM_PROP);
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(MY_PROPERTIES.getProperty("from"), "Nos Idées de Cadeaux"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject(subject);
                message.setContent(htmlText, "text/html; charset=UTF-8");

                Transport.send(message);
                logger.info("Sent message successfully....");

            } catch (MessagingException | UnsupportedEncodingException mex) {
                logger.warn(mex);
            }
        });
    }

    public static Future<?> sendEmailReinitializationPwd(String to, int userId, int tokenId) {
        String body = MY_PROPERTIES.get("body_reinitialization").toString();
        body = body.replaceAll("\\$\\$parameters\\$\\$",
                               MessageFormat.format("userIdParam={0}&tokenId={1}", userId, tokenId + ""));
        return sendEmail(to, "Mot de passe oublié - Nos idées de cadeaux", body);
    }

    static {
        try {
            InputStream input = EmailSender.class.getResourceAsStream("/mail.properties");
            assert input != null;
            MY_PROPERTIES.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            logger.trace("host: " + MY_PROPERTIES.getProperty("host"));
            logger.trace("from: " + MY_PROPERTIES.getProperty("from"));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Une erreur est survenue...", e);
        }
        SYSTEM_PROP.setProperty("mail.smtp.host", MY_PROPERTIES.getProperty("host"));
    }
}
