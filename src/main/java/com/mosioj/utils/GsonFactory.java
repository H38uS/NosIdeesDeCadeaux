package com.mosioj.utils;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mosioj.utils.date.LocalDateAdapter;
import com.mosioj.utils.date.TimestampAdapter;

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
	 * 
	 * @return The GSon object used to serialize.
	 */
	public static Gson getIt() {
		if (instance == null) {
			instance = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
										.setDateFormat(DATE_FORMAT)
										.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
										.registerTypeAdapter(Timestamp.class, new TimestampAdapter())
										.create();
		}
		return instance;
	}
}
