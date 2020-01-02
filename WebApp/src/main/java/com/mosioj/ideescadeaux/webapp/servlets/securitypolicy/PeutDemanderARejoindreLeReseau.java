package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.UserSecurityChecker;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
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
            Optional<Integer> toBeSentTo = ParametersUtils.readInt(request, userParameter);
            if (!toBeSentTo.isPresent()) {
                lastReason = "Aucun utilisateur trouvé en paramètre.";
                return false;
            }

            potentialFriend = UsersRepository.getUser(toBeSentTo.get()).orElseThrow(SQLException::new);
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
