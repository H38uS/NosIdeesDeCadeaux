package com.mosioj.ideescadeaux.webapp.servlets.controllers.compte;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.CompteInteractions;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import com.mosioj.ideescadeaux.webapp.viewhelper.CaptchaHandler;
import com.mosioj.ideescadeaux.webapp.viewhelper.EmptyFilter;
import com.mosioj.ideescadeaux.webapp.viewhelper.LoginHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/public/creation_compte")
public class CreationCompte extends IdeesCadeauxGetAndPostServlet<AllAccessToPostAndGet> {

    public static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";
    public static final String SUCCES_URL = "/public/succes_creation.jsp";
    public static final String FORM_URL = "/public/creation_compte.jsp";
    private static final Logger logger = LogManager.getLogger(CreationCompte.class);

    /** Class contructor. */
    public CreationCompte() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        RootingsUtils.rootToPage(FORM_URL, req, resp);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        HttpSession session = request.getSession();
        CompteInteractions helper = new CompteInteractions();

        // Récupération des paramètres
        String pwd = ParametersUtils.getPOSTParameterAsString(request, "pwd");
        String email = ParametersUtils.readAndEscape(request, "email", true).trim();
        String nameParam = ParametersUtils.readAndEscape(request, "pseudo", true).trim();

        // Validation des paramètres
        List<String> pwdErrors = helper.getValidatorPwd(pwd).getErrors();
        request.setAttribute("pwd_errors", pwdErrors);

        List<String> emailErrors = helper.checkEmail(helper.getValidatorEmail(email), -1, false); // The user does not
        // exist yet
        request.setAttribute("email_errors", emailErrors);

        String captchaResponse = ParametersUtils.getPOSTParameterAsString(request, "g-recaptcha-response");
        String urlCalled = request.getRequestURL().toString();
        logger.debug(captchaResponse + " / " + request.getRequestURL());
        boolean captchaOk = urlCalled.startsWith(HTTP_LOCALHOST_8080) || CaptchaHandler.resolveIt(captchaResponse);
        if (!captchaOk) {
            request.setAttribute("captcha_errors", "Erreur lors de la validation du Captcha.");
        }

        // Password hash
        String hashPwd = CompteInteractions.hashPwd(pwd);

        // Retour au formulaire si un paramètre est incorrect
        if (!pwdErrors.isEmpty() || !emailErrors.isEmpty() || !captchaOk) {
            RootingsUtils.rootToPage(FORM_URL, request, response);
            return;
        }

        // Les paramètres sont ok, on s'occupe de la requête
        final String name = nameParam.trim().isEmpty() ? email : nameParam;
        UsersRepository.addNewPersonne(email, hashPwd, name);
        session.invalidate();
        request.login(email, pwd);
        request.setAttribute("user", name);
        try {
            new LoginHelper().doFilter(request, response, new EmptyFilter());
        } catch (IOException e) {
            throw new ServletException(e.getMessage());
        }

        NotificationsRepository.notifyAboutANewInscription("A person within the site !! This is " + email + ".");
        Object connectedUser = request.getAttribute("connected_user");
        if (connectedUser instanceof User) {
            NType.NO_IDEA.buildDefault().sendItTo((User) connectedUser);
        }
        RootingsUtils.rootToPage(SUCCES_URL, request, response);
    }
}
