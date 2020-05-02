package com.mosioj.ideescadeaux.core.utils.date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.sql.Timestamp;

public class TimestampAdapter implements JsonSerializer<Timestamp> {

    @Override
    public JsonElement serialize(Timestamp date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(MyDateFormatViewer.formatMine(date));
    }

}
