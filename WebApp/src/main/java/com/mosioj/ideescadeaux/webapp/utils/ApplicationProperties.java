package com.mosioj.ideescadeaux.webapp.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ApplicationProperties.class);

    /** The application properties */
    private static final Properties prop;

    /** Utility class */
    private ApplicationProperties() {
        // Forbidden
    }

    /**
     * @return The application properties.
     */
    public static Properties getProp() {
        return prop;
    }

    static {
        prop = new Properties();
        try {
            prop.load(RootingsUtils.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
