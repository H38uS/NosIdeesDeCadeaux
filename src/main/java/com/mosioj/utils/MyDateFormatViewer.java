package com.mosioj.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyDateFormatViewer extends SimpleDateFormat {

	private static final long serialVersionUID = 3200032903715571847L;
	private static final Logger logger = LogManager.getLogger(MyDateFormatViewer.class);
	
	/**
	 * Attention: n'utiliser que pour visualiser !!
	 * 
	 * @param pattern
	 */
	public MyDateFormatViewer(String pattern) {
		super(pattern, Locale.FRANCE);
		calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		logger.trace(calendar.getTimeZone());
		setTimeZone(calendar.getTimeZone());
	}

}
