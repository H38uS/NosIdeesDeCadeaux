package com.mosioj.ideescadeaux.core.model;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationFactory;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;

public class TestNotificationFactory {

	private final Logger logger = LogManager.getLogger(TestNotificationFactory.class);

	@Test
	public void test() throws SQLException {
		for (NotificationType type : NotificationType.values()) {
			logger.info(MessageFormat.format("Testing creation of type: {0}", type));
			@SuppressWarnings("unused")
			AbstractNotification n = NotificationFactory.buildIt(	42,
																	1,
																	type.name(),
																	"Toto à la plage",
																	null,
																	true,
																	null,
																	new HashMap<ParameterName, Object>());
		}
	}

}
