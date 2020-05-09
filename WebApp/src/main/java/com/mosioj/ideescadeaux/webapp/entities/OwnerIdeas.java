package com.mosioj.ideescadeaux.webapp.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;

import java.util.List;

public class OwnerIdeas {

    @Expose
    private final User owner;

    @Expose
    private final List<DecoratedWebAppIdea> ideas;

    public OwnerIdeas(User owner, List<DecoratedWebAppIdea> ideas) {
        this.owner = owner;
        this.ideas = ideas;
    }

    /**
     * Factory method.
     *
     * @param owner The owner.
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
