package com.mosioj.ideescadeaux.core.model.entities.text;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.utils.Escaper;
import org.apache.commons.text.StringEscapeUtils;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class EntityWithText {

    /** Le text tel que rentré par l'utilisateur. N'est pas échappé. */
    @Column(name = "text")
    @Type(type = "text")
    protected String text;

    /** Le text échappé de l'utilisateur, converti en markdown. */
    @Transient
    @Expose
    private String htmlText;

    @PostLoad
    public void textPostLoad() {
        this.text = Escaper.transformCodeToSmiley(text);
        this.htmlText = Escaper.interpreteMarkDown(text);
    }

    /**
     * @return The idea text stored in DB, that will be presented to the browser.
     */
    public String getHtml() {
        return htmlText;
    }

    /**
     * @return The text displayed in textarea, with \n.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The raw text value.
     */
    public void setText(String text) {
        text = StringEscapeUtils.unescapeHtml4(text);
        text = Escaper.escapeIdeaText(text);
        this.text = Escaper.transformSmileyToCode(text);
    }
}
