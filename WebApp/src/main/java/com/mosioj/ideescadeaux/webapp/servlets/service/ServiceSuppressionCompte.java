package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.SuppressionCompte;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

@WebServlet("/protected/administration/service/supprimer_compte")
public class ServiceSuppressionCompte extends ServicePost<SuppressionCompte> {

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
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {

        ServiceResponse<String> ans;
        try {
            User user = policy.getUserToDelete();
            logger.info(MessageFormat.format("Suppression du compte {0} (id: {1})", user, user.id));
            UsersRepository.deleteUser(user);
            ans = ServiceResponse.ok(thisOne);
        } catch (Exception e) {
            ans = ServiceResponse.ko(e.getMessage(), thisOne);
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        buildResponse(response, ans);
    }

}
