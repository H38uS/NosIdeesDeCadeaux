package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.UserChangePwdRequest;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.logichelpers.CompteInteractions;
import com.mosioj.servlets.securitypolicy.PasswordChangeRequest;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/public/changer_mot_de_passe_depuis_reinit")
public class ChangerMotDePasseDepuisReinit extends IdeesCadeauxServlet<PasswordChangeRequest> {

	private static final long serialVersionUID = 5998641192324526001L;
	public static final String VIEW_PAGE_URL = "/public/changer_mot_de_passe_depuis_reinit.jsp";
	public static final String tokenParameter = "tokenId";
	public static final String userIdParameter = "userIdParam";
	private static final UserChangePwdRequest ucpr = new UserChangePwdRequest();
	private static final String SUCCES_PAGE_URL = "/public/changer_mot_de_passe_depuis_reinit_succes.jsp";

	public ChangerMotDePasseDepuisReinit() {
		super(new PasswordChangeRequest(ucpr, tokenParameter, userIdParameter));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		req.setAttribute(tokenParameter, req.getParameter(tokenParameter));
		req.setAttribute(userIdParameter, req.getParameter(userIdParameter));
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		
		CompteInteractions helper = new CompteInteractions();

		request.setAttribute(tokenParameter, ParametersUtils.readInt(request, tokenParameter));
		int userId = ParametersUtils.readInt(request, userIdParameter).get();
		request.setAttribute(userIdParameter, userId);
		
		String pwd1 = ParametersUtils.readAndEscape(request, "pwd1").trim();
		String pwd2 = ParametersUtils.readAndEscape(request, "pwd2").trim();
		
		List<String> pwdErrors1 = helper.checkPwd(helper.getValidatorPwd(pwd1));
		List<String> pwdErrors2 = helper.checkPwd(helper.getValidatorPwd(pwd2));
		if (!pwd1.equals(pwd2)) {
			pwdErrors2.add("Les deux mots de passe ne correspondent pas.");
		}
		
		String digested = helper.hashPwd(pwd1, pwdErrors1);
		
		if (!pwdErrors1.isEmpty() || !pwdErrors2.isEmpty()) {
			request.setAttribute("pwd1_error", pwdErrors1);
			request.setAttribute("pwd2_error", pwdErrors2);
			RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
			return;
		}

		UserChangePwdRequest changePwdRequest = new UserChangePwdRequest();
		changePwdRequest.deleteAssociation(userId);
		model.users.updatePassword(userId, digested);

		RootingsUtils.rootToPage(SUCCES_PAGE_URL, request, response);
	}

}
