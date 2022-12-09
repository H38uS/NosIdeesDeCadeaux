package com.mosioj.ideescadeaux.webapp.servlets.controllers.compte;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserChangePwdRequestRepository;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.CompteInteractions;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.PasswordChangeRequest;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/public/changer_mot_de_passe_depuis_reinit")
public class ChangerMotDePasseDepuisReinit extends IdeesCadeauxGetAndPostServlet<PasswordChangeRequest> {

    public static final String VIEW_PAGE_URL = "/public/changer_mot_de_passe_depuis_reinit.jsp";
    public static final String tokenParameter = "tokenId";
    public static final String userIdParameter = "userIdParam";
    public static final String SUCCES_PAGE_URL = "/public/changer_mot_de_passe_depuis_reinit_succes.jsp";

    public ChangerMotDePasseDepuisReinit() {
        super(new PasswordChangeRequest(tokenParameter, userIdParameter));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
        req.setAttribute(tokenParameter, ParametersUtils.getGETParameterAsString(req, tokenParameter));
        req.setAttribute(userIdParameter, ParametersUtils.getGETParameterAsString(req, userIdParameter));
        RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        CompteInteractions helper = new CompteInteractions();
        User user = policy.getUser();

        request.setAttribute(tokenParameter, policy.getTokenId());
        request.setAttribute(userIdParameter, user.id);

        String pwd1 = ParametersUtils.getPOSTParameterAsString(request, "pwd1").trim();
        String pwd2 = ParametersUtils.getPOSTParameterAsString(request, "pwd2").trim();

        List<String> pwdErrors1 = helper.getValidatorPwd(pwd1).getErrors();
        List<String> pwdErrors2 = helper.getValidatorPwd(pwd2).getErrors();
        if (!pwd1.equals(pwd2)) {
            pwdErrors2.add("Les deux mots de passe ne correspondent pas.");
        }

        String digested = CompteInteractions.hashPwd(pwd1);

        if (!pwdErrors1.isEmpty() || !pwdErrors2.isEmpty()) {
            request.setAttribute("pwd1_error", pwdErrors1);
            request.setAttribute("pwd2_error", pwdErrors2);
            RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
            return;
        }

        UserChangePwdRequestRepository.deleteAssociation(user.id);
        user.setPassword(digested);
        HibernateUtil.update(user);

        RootingsUtils.rootToPage(SUCCES_PAGE_URL, request, response);
    }

}
