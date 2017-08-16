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

	private static boolean isInitialized = false;
	private static final Logger logger = LogManager.getLogger(EmailSender.class);
	private static Properties p;

	private static void initialize() {
		p = new Properties();
		try {
			InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("mail.properties");
			p.load(new InputStreamReader(input, "UTF-8"));
			logger.debug("host: " + p.getProperty("host"));
			logger.debug("from: " + p.getProperty("from"));
			isInitialized = true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public static void sendEmailReinitializationPwd(String to, int userId, int tokenId) {

		if (!isInitialized) {
			initialize();
		}

		// props.setProperty("mail.user", "myuser");
		// props.setProperty("mail.password", "mypwd");

		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", p.getProperty("host"));
		Session session = Session.getDefaultInstance(properties);

		try {
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(p.getProperty("from"), "Nos Idées de Cadeaux"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("Mot de passe oublié - Nos idées de cadeaux");

			String body = p.get("body_reinitialization").toString();
			body = body.replaceAll("\\$\\$parameters\\$\\$", MessageFormat.format("userIdParam={0}&tokenId={1}", userId, tokenId + ""));
			message.setContent(body, "text/html; charset=UTF-8");

			Transport.send(message);
			System.out.println("Sent message successfully....");

		} catch (MessagingException | UnsupportedEncodingException mex) {
			mex.printStackTrace();
		}
	}

}
