package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;

import java.sql.Time;
import java.util.List;

public class RelationSuggestion {

    @Expose
    public User suggestedBy;

    @Expose
    public User suggestedTo;

    @Expose
    public List<User> suggestions;

    private final Time suggestedDate;

    public RelationSuggestion(User suggestedBy, User suggestedTo, List<User> suggestions, Time suggestedDate) {
        this.suggestedBy = suggestedBy;
        this.suggestedTo = suggestedTo;
        this.suggestions = suggestions;
        this.suggestedDate = suggestedDate;
    }

    public User getSuggestedBy() {
        return suggestedBy;
    }

    public User getSuggestedTo() {
        return suggestedTo;
    }

    public List<User> getSuggestions() {
        return suggestions;
    }

    public Time getSuggestedDate() {
        return suggestedDate;
    }

}
