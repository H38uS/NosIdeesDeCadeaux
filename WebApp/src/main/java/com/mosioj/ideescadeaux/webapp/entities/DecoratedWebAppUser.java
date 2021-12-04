package com.mosioj.ideescadeaux.webapp.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;

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

    public DecoratedWebAppUser(final User user, final User connectedUser) {
        this.user = user;
        isInMyNetwork = associationExists(user.id, connectedUser.id);
        readableBirthday = user.getBirthdayAsString();
        hasSentARequest = UserRelationRequestsRepository.associationExists(connectedUser.id, user.id);
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
