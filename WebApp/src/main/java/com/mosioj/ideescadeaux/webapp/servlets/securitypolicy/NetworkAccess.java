package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class NetworkAccess extends SecurityPolicy implements UserSecurityChecker {

    /** Defines the string used in HttpServletRequest to retrieve the user id. */
    private final String userParameter;

    private User friend;

    /**
     * @param userParameter The request parameter name.
     */
    public NetworkAccess(String userParameter) {
        this.userParameter = userParameter;
    }

    private boolean hasAccess(HttpServletRequest request) {

        friend = ParametersUtils.readInt(request, userParameter).flatMap(UsersRepository::getUser).orElse(connectedUser);
        if (friend == null) {
            lastReason = "Aucun utilisateur trouvé en paramètre.";
            return false;
        }

        final boolean isMe = connectedUser.equals(friend);
        final boolean res = isMe || UserRelationsRepository.associationExists(friend.id, connectedUser.id);
        if (!res) {
            lastReason = "Vous n'êtes pas ami avec cette personne.";
            return false;
        }

        return true;
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        return hasAccess(request);
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        return hasAccess(request);
    }

    @Override
    public User getUser() {
        return friend;
    }

    @Override
    public void reset() {
        friend = null;
    }

}
