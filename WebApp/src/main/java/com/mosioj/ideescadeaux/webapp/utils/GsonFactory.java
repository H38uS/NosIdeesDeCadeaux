package com.mosioj.ideescadeaux.webapp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mosioj.ideescadeaux.core.utils.date.LocalDateAdapter;

import java.time.LocalDate;

public class GsonFactory {

    public static final String DATE_FORMAT = "dd/MM/YYYY";

    /**
     * The only instance.
     */
    private static Gson instance;

    private GsonFactory() {
        // Not allowed
    }

    /**
     * @return The GSon object used to serialize.
     */
    public static Gson getIt() {
        if (instance == null) {
            instance = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                        .setDateFormat(DATE_FORMAT)
                                        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                        .create();
        }
        return instance;
    }
}
