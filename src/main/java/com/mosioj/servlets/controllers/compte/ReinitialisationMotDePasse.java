package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.UserChangePwdRequest;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.logichelpers.CompteInteractions;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.EmailSender;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/public/reinitialiser_mot_de_passe")
public class ReinitialisationMotDePasse extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 5998641192324526001L;
	public static final String VIEW_PAGE_URL = "/public/reinitialiser_mot_de_passe.jsp";
	public static final String SUCCES_PAGE_URL = "/public/reinitialiser_mot_de_passe_succes.jsp";

	public ReinitialisationMotDePasse() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		
		CompteInteractions helper = new CompteInteractions();

		String email1 = ParametersUtils.readAndEscape(request, "email1").trim();
		String email2 = ParametersUtils.readAndEscape(request, "email2").trim();

		List<String> emailErrors1 = helper.checkEmail(helper.getValidatorEmail(email1), -1, true);
		List<String> emailErrors2 = helper.checkEmail(helper.getValidatorEmail(email2), -1, true);
		if (!email1.equals(email2)) {
			emailErrors2.add("Les deux emails ne correspondent pas.");
		}
		
		if (!emailErrors1.isEmpty() || !emailErrors2.isEmpty()) {
			request.setAttribute("email1", email1);
			request.setAttribute("email2", email2);
			request.setAttribute("email1_error", emailErrors1);
			request.setAttribute("email2_error", emailErrors2);
			RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
			return;
		}
		
		int userId = users.getId(email1);
		int token = new Random().nextInt();
		UserChangePwdRequest changePwdRequest = new UserChangePwdRequest();
		changePwdRequest.deleteAssociation(userId);
		changePwdRequest.createNewRequest(userId, token);

		EmailSender.sendEmailReinitializationPwd(email1, userId, token);

		request.setAttribute("email", email1);
		RootingsUtils.rootToPage(SUCCES_PAGE_URL, request, response);
	}

}
