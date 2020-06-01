package com.mosioj.ideescadeaux.webapp.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

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
        readableBirthday = user.getBirthday().map(b -> MyDateFormatViewer.formatDayWithYearHidden(b.getTime()))
                               .orElse("- on ne sait pas...");
        hasSentARequest = UserRelationRequestsRepository.associationExists(connectedUser.id, user.id);
    }
}
