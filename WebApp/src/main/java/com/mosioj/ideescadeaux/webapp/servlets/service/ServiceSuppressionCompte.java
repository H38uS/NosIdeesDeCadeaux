package com.mosioj.ideescadeaux.webapp.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.SuppressionCompte;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.User;

@WebServlet("/protected/administration/service/supprimer_compte")
public class ServiceSuppressionCompte extends IdeesCadeauxPostServlet<SuppressionCompte> {

    private static final long serialVersionUID = -8612163046284587669L;
    private static final Logger logger = LogManager.getLogger(ServiceSuppressionCompte.class);

    public static final String USER_ID_PARAM = "userId";

    /**
     * Seuls les admins peuvent le faire
     */
    public ServiceSuppressionCompte() {
        super(new SuppressionCompte(USER_ID_PARAM));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        ServiceResponse<String> ans;
        try {
            User user = policy.getUserToDelete();
            logger.info(MessageFormat.format("Suppression du compte {0} (id: {1})", user, user.id));
            UsersRepository.deleteUser(user);
            ans = ServiceResponse.ok(isAdmin(request));
        } catch (Exception e) {
            ans = ServiceResponse.ko(e.getMessage(), isAdmin(request));
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        buildResponse(response, ans);
    }

}
