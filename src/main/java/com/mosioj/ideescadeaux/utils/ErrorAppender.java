package com.mosioj.ideescadeaux.utils;

import java.text.MessageFormat;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import com.mosioj.ideescadeaux.model.repositories.Notifications;

@Plugin(name = "ErrorAppender", category = "Core", elementType = "appender")
public class ErrorAppender extends AbstractAppender {

	private static final long serialVersionUID = -1758361043994005600L;
	private final Notifications n = new Notifications();

	public ErrorAppender(String name, Filter filter) {
		super(name, filter, null);
	}

	@PluginFactory
	public static ErrorAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Filter") Filter filter) {
		return new ErrorAppender(name, filter);
	}

	@Override
	public void append(LogEvent event) {
		if (!RootingsUtils.shouldLogStack()) {
			// Si on ne log pas, on doit envoyer un email
			n.notifyAboutAnError(event.getMessage().toString());
		} else {
			// Pas de logger, sinon boucle infinie
			System.out.println(MessageFormat.format("Envoie de mail en mode PROD sur l''évènement suivant: {0}", event));
		}
	}

}
