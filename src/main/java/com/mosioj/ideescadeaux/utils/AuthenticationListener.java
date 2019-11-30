package com.mosioj.ideescadeaux.utils;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.PublicInvocationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;

import com.mosioj.ideescadeaux.model.table.Users;

public class AuthenticationListener implements ApplicationListener<ApplicationEvent> {

	private static final Logger logger = LogManager.getLogger(AuthenticationListener.class);

	@Override
	public void onApplicationEvent(ApplicationEvent appEvent) {

		Class<? extends ApplicationEvent> clazz = appEvent.getClass();
		logger.trace("Event => " + clazz);

		if (!AbstractAuthenticationEvent.class.isAssignableFrom(clazz) && !AbstractAuthorizationEvent.class.isAssignableFrom(clazz)) {
			// Only interesting into authorization / authentication
			return;
		}

		// Request for a protected page when not logged in
		// Or about public
		if (appEvent instanceof AuthorizationFailureEvent || appEvent instanceof PublicInvocationEvent
				|| appEvent instanceof SessionFixationProtectionEvent || appEvent instanceof InteractiveAuthenticationSuccessEvent) {
			// Nothing special then
			return;
		}

		// Connection event
		if (appEvent instanceof AuthenticationSuccessEvent) {
			AuthenticationSuccessEvent event = (AuthenticationSuccessEvent) appEvent;
			String email = event.getAuthentication().getName();
			logger.info(MessageFormat.format(	"{0} vient de se connecter. Détails: {1}",
												email,
												event.getAuthentication().getDetails()));
			new Users().touch(email);
			return;
		}

		// Invalid credentials
		if (appEvent instanceof AuthenticationFailureBadCredentialsEvent) {
			AuthenticationFailureBadCredentialsEvent event = (AuthenticationFailureBadCredentialsEvent) appEvent;
			logger.warn(MessageFormat.format(	"Tentative de connexion de {0} (depuis {1}) avec les mauvais login/mdp.",
												event.getAuthentication().getName(),
												event.getSource()));
			logger.warn(MessageFormat.format("Détails : {0}", event.getAuthentication().getDetails()));
			return;
		}

		logger.error("Unknown event => " + clazz);
	}

}
