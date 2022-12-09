package com.mosioj.ideescadeaux.webapp.servlets.service.response;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.text.StringEscapeUtils;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NameAnswer that = (NameAnswer) o;

        return new EqualsBuilder().append(value, that.value)
                                  .append(email, that.email)
                                  .append(label, that.label)
                                  .append(imgsrc, that.imgsrc)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(value).append(email).append(label).append(imgsrc).toHashCode();
    }
}
