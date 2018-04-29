package com.mosioj.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyDateFormat extends SimpleDateFormat {

	private static final long serialVersionUID = 3200032903715571847L;
	
	public MyDateFormat(String pattern) {
		super(pattern, Locale.FRANCE);
		setTimeZone(calendar.getTimeZone());
	}

}
