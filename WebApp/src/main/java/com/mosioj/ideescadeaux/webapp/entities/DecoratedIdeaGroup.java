package com.mosioj.ideescadeaux.webapp.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroupContent;
import com.mosioj.ideescadeaux.core.model.entities.User;

public class DecoratedIdeaGroup {

    @Expose
    private final IdeaGroup group;

    /** The connected user share for this group. Can be null if he does not participate yet. */
    @Expose
    private final IdeaGroupContent userIdeaGroupContent;

    @Expose
    private final boolean isInGroup;

    @Expose
    private final double currentTotal;

    @Expose
    private final String remaining;

    /**
     * @param group         The underlying group.
     * @param connectedUser The user connected when doing this request.
     */
    public DecoratedIdeaGroup(IdeaGroup group, User connectedUser) {
        this.group = group;
        this.userIdeaGroupContent = group.getShares()
                                         .stream()
                                         .filter(s -> connectedUser.equals(s.getUser()))
                                         .findAny()
                                         .orElse(null);
        this.isInGroup = userIdeaGroupContent != null;
        this.currentTotal = group.getShares().stream().map(IdeaGroupContent::getAmount).reduce(Double::sum).orElse(.0);
        final double left = group.total - currentTotal;
        remaining = String.format("%1$,.2f", left);
    }
}
