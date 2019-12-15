package com.mosioj.ideescadeaux.servlets.service.response;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.model.entities.User;
import org.apache.commons.lang3.StringEscapeUtils;

public class NameAnswer {

    @Expose
    public final String value;

    @Expose
    private final String email;

    @Expose
    private final String label;

    @Expose
    private final String imgsrc;

    public NameAnswer(User user) {
        value = StringEscapeUtils.unescapeHtml4(user.getLongNameEmail());
        email = StringEscapeUtils.unescapeHtml4(user.getEmail());
        label = StringEscapeUtils.unescapeHtml4(user.getEmail());
        imgsrc = "protected/files/uploaded_pictures/avatars/" + user.getAvatarSrcSmall();
    }
}
