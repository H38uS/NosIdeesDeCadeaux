package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class PeutDemanderARejoindreLeReseau extends SecurityPolicy implements UserSecurityChecker {

    private final String userParameter;
    private User potentialFriend;

    /**
     *
     */
    public PeutDemanderARejoindreLeReseau(String userParameter) {
        this.userParameter = userParameter;
    }

    private boolean hasAccess(HttpServletRequest request) {

        potentialFriend = ParametersUtils.readInt(request, userParameter)
                                         .flatMap(UsersRepository::getUser)
                                         .orElse(null);
        if (potentialFriend == null) {
            lastReason = "Aucun utilisateur trouvé en paramètre.";
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
        return potentialFriend;
    }

    @Override
    public void reset() {
        potentialFriend = null;
    }
}
