package com.mosioj.ideescadeaux.servlets.securitypolicy;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public final class PeutDemanderARejoindreLeReseau extends SecurityPolicy implements UserSecurityChecker {

    private static final Logger logger = LogManager.getLogger(PeutDemanderARejoindreLeReseau.class);

    private final String userParameter;
    private User potentialFriend;

    /**
     *
     */
    public PeutDemanderARejoindreLeReseau(String userParameter) {
        this.userParameter = userParameter;
    }

    private boolean hasAccess(HttpServletRequest request) {

        try {

            // Y a-t-il un utilisateur ?
            // FIXME : 2 pour toutes les polices qui récupèrent un paramètre, vérifier que ça existe en base (e.g. pour
            // USERS)
            Optional<Integer> toBeSentTo = readInt(request, userParameter);
            if (!toBeSentTo.isPresent()) {
                lastReason = "Aucun utilisateur trouvé en paramètre.";
                return false;
            }

            potentialFriend = model.users.getUser(toBeSentTo.get());
            return true;

        } catch (Exception e) {
            logger.error("Unable to process the security check: " + e.getMessage());
            lastReason = "Une erreur est survenue lors de la vérification des droits. Veuillez réessayer, ou contacter l'administrateur.";
            return false;
        }
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
