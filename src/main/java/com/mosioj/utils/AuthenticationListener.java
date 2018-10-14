package com.mosioj.utils;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import com.mosioj.model.table.Users;

public class AuthenticationListener implements ApplicationListener<ApplicationEvent> {

	private static final Logger logger = LogManager.getLogger(AuthenticationListener.class);

	@Override
	public void onApplicationEvent(ApplicationEvent appEvent) {
		if (appEvent instanceof AuthenticationSuccessEvent) {
			AuthenticationSuccessEvent event = (AuthenticationSuccessEvent) appEvent;
			String email = event.getAuthentication().getName();
			logger.info(MessageFormat.format("{0} vient de se connecter.", email));
			new Users().touch(email);
		} else if (appEvent instanceof AuthorizationFailureEvent) {
			AuthorizationFailureEvent event = (AuthorizationFailureEvent) appEvent;
			logger.warn(MessageFormat.format(	"Tentative de connexion (depuis {1}) avec l''erreur suivante: {0}.",
												event.getAccessDeniedException().getMessage(),
												event.getSource()));
			logger.warn(MessageFormat.format("Détails : {0}", event.getAuthentication().getDetails()));
		}
	}

}
