package com.mosioj.ideescadeaux.webapp.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;

import java.util.List;

/**
 * Association between a user and its idea list.
 */
public class OwnerIdeas {

    @Expose
    private final User owner;

    /** True if this idea list is the deleted ones */
    @Expose
    private final boolean isDeletedIdeas;

    @Expose
    private final List<DecoratedWebAppIdea> ideas;

    public OwnerIdeas(User owner, List<DecoratedWebAppIdea> ideas) {
        this.owner = owner;
        this.ideas = ideas;
        this.isDeletedIdeas = ideas.stream()
                                   .findAny()
                                   .map(DecoratedWebAppIdea::getIdee)
                                   .map(Idee::isDeleted)
                                   .orElse(false);
    }

    /**
     * Factory method.
     *
     * @param owner The owner of those ideas.
     * @param ideas His list of ideas.
     * @return The combined object.
     */
    public static OwnerIdeas from(User owner, List<DecoratedWebAppIdea> ideas) {
        return new OwnerIdeas(owner, ideas);
    }

    /**
     * @return the owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * @return the ideas
     */
    public List<DecoratedWebAppIdea> getIdeas() {
        return ideas;
    }
}
