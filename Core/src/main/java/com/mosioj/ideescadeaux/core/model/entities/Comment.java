package com.mosioj.ideescadeaux.core.model.entities;

import com.mosioj.ideescadeaux.core.utils.Escaper;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

import java.sql.Timestamp;

public class Comment {

    private final int id;
    private final String text;

    /**
     * Le text échappé de l'utilisateur, converti en markdown.
     */
    private final String htmlText;

    private final User writtenBy;
    private final int idea;
    private final Timestamp time;

    public Comment(int id, String text, User writtenBy, int idea, Timestamp time) {
        super();
        this.id = id;
        this.text = text;
        htmlText = Escaper.interpreteMarkDown(text);
        this.writtenBy = writtenBy;
        this.idea = idea;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    /**
     * @return The idea text stored in DB, that will be presented to the browser.
     */
    public String getHtml() {
        return htmlText;
    }

    public User getWrittenBy() {
        return writtenBy;
    }

    public int getIdea() {
        return idea;
    }

    public String getTime() {
        return MyDateFormatViewer.formatMine(time);
    }
}
