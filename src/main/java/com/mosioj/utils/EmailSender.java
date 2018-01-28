package com.mosioj.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailSender {

	private static final Logger logger = LogManager.getLogger(EmailSender.class);
	private static Properties p;

	private static void initialize() {
		p = new Properties();
		try {
			InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("mail.properties");
			p.load(new InputStreamReader(input, "UTF-8"));
			logger.debug("host: " + p.getProperty("host"));
			logger.debug("from: " + p.getProperty("from"));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	/**
	 * 
	 * @return The properties.
	 */
	private static Properties getP() {
		if (p == null) {
			initialize();
		}
		return p;
	}
	
	/**
	 * Sends out an email.
	 * 
	 * @param to The email address where to send the email.
	 * @param subject The email subject.
	 * @param htmlText The email body, html formated.
	 */
	public static void sendEmail(String to, String subject, String htmlText) {

		logger.info(MessageFormat.format("Sending email to {0}...", to));

		// props.setProperty("mail.user", "myuser");
		// props.setProperty("mail.password", "mypwd");

		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", getP().getProperty("host"));
		Session session = Session.getDefaultInstance(properties);

		try {
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(getP().getProperty("from"), "Nos Idées de Cadeaux"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setContent(htmlText, "text/html; charset=UTF-8");

			Transport.send(message);
			logger.info("Sent message successfully....");

		} catch (MessagingException | UnsupportedEncodingException mex) {
			mex.printStackTrace();
			logger.error(mex.getMessage());
		}
	}

	public static void sendEmailReinitializationPwd(String to, int userId, int tokenId) {
		String body = getP().get("body_reinitialization").toString();
		body = body.replaceAll("\\$\\$parameters\\$\\$", MessageFormat.format("userIdParam={0}&tokenId={1}", userId, tokenId + ""));
		sendEmail(to, "Mot de passe oublié - Nos idées de cadeaux", body);
	}

}
