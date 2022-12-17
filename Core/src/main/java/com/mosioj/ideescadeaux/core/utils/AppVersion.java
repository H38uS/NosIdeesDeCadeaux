package com.mosioj.ideescadeaux.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AppVersion {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(AppVersion.class);

    /**
     * Class constructor.
     */
    private AppVersion() {
        // Forbidden
    }

    /** The application version */
    public static final String DA_VERSION = readTheVersionFromFile();

    private static String readTheVersionFromFile() {
        try (InputStream stream = AppVersion.class.getResourceAsStream("/version.txt")) {
            if (stream == null) {
                logger.error("Null stream for the file version...");
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                    return br.readLine().trim();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            logger.error("Exception while reading the file version", e);
        }
        return "UNKNOWN";
    }

}
