package com.mosioj.ideescadeaux.webapp.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.webapp.repositories.IdeasWithInfoRepository;

import javax.persistence.Transient;
import java.util.Objects;

import static com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository.associationExists;

public class DecoratedWebAppUser {

    /** The embedded user. */
    @Expose
    private final User user;

    /** Whether the user belongs to the network. */
    @Expose
    private final boolean isInMyNetwork;

    /** The user's birthday, as a string. */
    @Expose
    private final String readableBirthday;

    /** Whether we have already sent a friendship request to this user. */
    @Expose
    public final boolean hasSentARequest;

    @Transient
    private final boolean hasBookedOneOfItsIdeas;
    // utilisé dans l'index en jsp - impossible de supprimer le getter pour l'instant - FIXME drop it

    public DecoratedWebAppUser(final User user, final User connectedUser) {
        this.user = user;
        isInMyNetwork = associationExists(user, connectedUser);
        readableBirthday = user.getBirthdayAsString();
        hasSentARequest = UserRelationRequestsRepository.associationExists(connectedUser, user);
        this.hasBookedOneOfItsIdeas = IdeasWithInfoRepository.getIdeasWhereIDoParticipateIn(connectedUser)
                                                             .parallelStream()
                                                             .map(Idee::getOwner)
                                                             .map(user::equals)
                                                             .filter(Boolean.TRUE::equals)
                                                             .findFirst()
                                                             .orElse(false);
    }

    /**
     * Used in several JSP, impossible to delete. FIXME faire un service pour supprimer
     *
     * @return True if the connected user has booked one of this user ideas, or is participating in a group.
     */
    public boolean getHasBookedOneOfItsIdeas() {
        return hasBookedOneOfItsIdeas;
    }

    /**
     * @return The underlying user.
     */
    public User getUser() {
        return user; // FIXME : faire un service pour l'index pour supprimer ça
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecoratedWebAppUser that = (DecoratedWebAppUser) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public String toString() {
        return "DecoratedWebAppUser{" +
               "user=" + user +
               ", isInMyNetwork=" + isInMyNetwork +
               ", readableBirthday='" + readableBirthday + '\'' +
               ", hasSentARequest=" + hasSentARequest +
               '}';
    }
}
