package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class PossibleSuggestion {

    @Expose
    public User possibleSuggestion;

    @Expose
    public boolean isPossible = true;

    @Expose
    public String reasonIfNotPossible = StringUtils.EMPTY;

    public PossibleSuggestion(User possibleSuggestion) {
        this.possibleSuggestion = possibleSuggestion;
    }

    /**
     * @param reason The reason why it is not possible.
     * @return The object.
     */
    public PossibleSuggestion withReason(String reason) {
        isPossible = false;
        reasonIfNotPossible = reason;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PossibleSuggestion that = (PossibleSuggestion) o;
        return isPossible == that.isPossible &&
               Objects.equals(possibleSuggestion, that.possibleSuggestion) &&
               Objects.equals(reasonIfNotPossible, that.reasonIfNotPossible);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibleSuggestion, isPossible, reasonIfNotPossible);
    }

    @Override
    public String toString() {
        return "PossibleSuggestion{" +
               "possibleSuggestion=" + possibleSuggestion +
               ", isPossible=" + isPossible +
               ", reasonIfNotPossible='" + reasonIfNotPossible + '\'' +
               '}';
    }
}
