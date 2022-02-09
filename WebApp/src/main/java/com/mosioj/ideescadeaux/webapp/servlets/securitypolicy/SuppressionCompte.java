package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SuppressionCompte extends SecurityPolicy {

    /**
     * Defines the string used in HttpServletRequest to retrieve the user id.
     */
    private final String userParameter;

    private User user;

    public SuppressionCompte(String userParameter) {
        this.userParameter = userParameter;
    }

    protected boolean canInteract(HttpServletRequest request) {

        user = ParametersUtils.readInt(request, userParameter).flatMap(UsersRepository::getUser).orElse(null);
        if (user == null) {
            lastReason = "Aucun utilisateur trouvé en paramètre.";
            return false;
        }

        if (!connectedUser.isAdmin()) {
            lastReason = "Non, mais non.";
            return false;
        }

        return true;
    }

    @Override
    public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
        return canInteract(request);
    }

    @Override
    public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) {
        return canInteract(request);
    }

    /**
     * @return The user to delete, or null if the checks have not passed.
     */
    public User getUserToDelete() {
        return user;
    }

    @Override
    public void reset() {
        user = null;
    }

}
