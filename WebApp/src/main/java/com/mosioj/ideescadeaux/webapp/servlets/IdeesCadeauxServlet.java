package com.mosioj.ideescadeaux.webapp.servlets;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.Compteur;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * An intermediate servlet for test purpose. Increase the visibility of tested method.
 *
 * @author Jordan Mosio
 */
public abstract class IdeesCadeauxServlet<P extends SecurityPolicy> extends HttpServlet {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(IdeesCadeauxServlet.class);

    /** The security policy defining whether we can interact with the parameters, etc. */
    protected final P policy;

    /** The connected user, or null if the user is not logged in. */
    protected User thisOne = null;

    /** The user device used to perform this http request. */
    protected Device device;

    /**
     * Class constructor.
     *
     * @param policy The security policy defining whether we can interact with the parameters, etc.
     */
    public IdeesCadeauxServlet(P policy) {
        this.policy = policy;
    }

    /**
     * Rooting action performed when the policy is not OK.
     * Default behavior is to root to the error page.
     *
     * @param policy The policy not met.
     */
    protected abstract void dealWithUnauthorizedPolicyAccess(HttpServletRequest request,
                                                             HttpServletResponse response,
                                                             P policy);

    /**
     * Internal class for GET processing, post security checks.
     *
     * @param request  The http request.
     * @param response The http response.
     */
    public abstract void ideesKDoGET(HttpServletRequest request,
                                     HttpServletResponse response) throws ServletException, SQLException, IOException;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        Locale.setDefault(Locale.Category.FORMAT, Locale.FRANCE);
        fillConnectedUserIfPossible(request);
        policy.setConnectedUser(thisOne);
        policy.reset();

        if (!policy.hasRightToInteractInGetRequest(request, response)) {

            int userId;
            try {
                userId = thisOne.id;
            } catch (Exception e) {
                userId = -1;
            }

            request.setAttribute("error_message", policy.getLastReason());
            logger.warn(MessageFormat.format("Inapropriate GET access from user {0} on {1}. Reason: {2}",
                                             userId,
                                             request.getRequestURL(),
                                             policy.getLastReason()));

            dealWithUnauthorizedPolicyAccess(request, response, policy);
            return;
        }

        device = (Device) request.getAttribute("device");

        if (request.getRemoteUser() != null) {
            try {
                // Mise à jour du nombre de notifications
                // FIXME : ne fonctionne pas pour les jsp... Faudrait faire un filtre dédié
                final Compteur count = new Compteur(NotificationsRepository.getUserNotificationCount(thisOne));
                ParentRelationshipRepository.getChildren(thisOne.id)
                                            .forEach(c -> count.add(NotificationsRepository.getUserNotificationCount(c)));
                request.setAttribute("notif_count", count.getValue());
            } catch (Exception e) {
                // Osef
                logger.warn(MessageFormat.format("Erreur lors de la récupération du user Id et des notif...{0}",
                                                 e.getMessage()));
                logger.warn(Arrays.toString(e.getStackTrace()));
            }
        }

        try {
            // Security has passed, perform the logic
            ideesKDoGET(request, response);
        } catch (SQLException | ServletException | IOException e) {
            // Default error management
            RootingsUtils.rootToGenericSQLError(thisOne, e, request, response);
        }
    }

    /**
     * Internal class for POST processing, post security checks.
     *
     * @param request  The http request.
     * @param response The http response.
     */
    public abstract void ideesKDoPOST(HttpServletRequest request,
                                      HttpServletResponse response) throws ServletException, SQLException, IOException;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

        Locale.setDefault(Locale.Category.FORMAT, Locale.FRANCE);
        fillConnectedUserIfPossible(request);
        policy.setConnectedUser(thisOne);
        policy.reset();

        if (!policy.hasRightToInteractInPostRequest(request, response)) {

            int userId;
            try {
                userId = thisOne.id;
            } catch (Exception e) {
                userId = -1;
            }

            request.setAttribute("error_message", policy.getLastReason());
            logger.warn(MessageFormat.format("Inapropriate POST access from user {0} on {1}. Reason: {2}",
                                             userId,
                                             request.getRequestURL(),
                                             policy.getLastReason()));

            dealWithUnauthorizedPolicyAccess(request, response, policy);
            return;
        }

        device = (Device) request.getAttribute("device");

        if (request.getRemoteUser() != null) {
            try {
                // Mise à jour du nombre de notifications
                User thisUser = thisOne;
                final Compteur count = new Compteur(NotificationsRepository.getUserNotificationCount(thisUser));
                ParentRelationshipRepository.getChildren(thisUser.id)
                                            .forEach(c -> count.add(NotificationsRepository.getUserNotificationCount(c)));
                request.setAttribute("notif_count", count.getValue());
            } catch (Exception e) {
                // Osef
                logger.warn(MessageFormat.format("Erreur lors de la récupération du user Id et des notif...{0}",
                                                 e.getMessage()));
                logger.warn(Arrays.toString(e.getStackTrace()));
            }
        }

        try {
            // Security has passed, perform the logic
            ideesKDoPOST(request, response);
        } catch (SQLException | ServletException | IOException e) {
            RootingsUtils.rootToGenericSQLError(thisOne, e, request, response);
        }
    }

    /**
     * If the user is connected, sets up the field.
     *
     * @param request The http request.
     */
    private void fillConnectedUserIfPossible(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object connectedUser = session.getAttribute("connected_user");
        if (connectedUser != null) {
            thisOne = (User) connectedUser;
        }
    }
}
