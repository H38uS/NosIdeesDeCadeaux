package com.mosioj.utils.date;

import java.lang.reflect.Type;
import java.sql.Timestamp;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TimestampAdapter implements JsonSerializer<Timestamp> {

	@Override
	public JsonElement serialize(Timestamp date, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(new MyDateFormatViewer(MyDateFormatViewer.DATETIME_DISPLAY_FORMAT).format(date));
	}

}
