package com.mosioj.ideescadeaux.core.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class AppVersion {

    /**
     * Class constructor.
     */
    private AppVersion() {
        // Forbidden
    }

    /** The application version */
    public static final String DA_VERSION = readTheVersionFromFile();

    private static String readTheVersionFromFile() {
        URL url = AppVersion.class.getResource("/version.txt");
        if (url != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(url.getFile()))) {
                return br.readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "UNKNOWN";
    }

}
