package com.mosioj.ideescadeaux.servlets.controllers.compte;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.table.UserChangePwdRequest;
import com.mosioj.ideescadeaux.servlets.logichelpers.CompteInteractions;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.EmailSender;
import com.mosioj.ideescadeaux.utils.ParametersUtils;
import com.mosioj.ideescadeaux.utils.RootingsUtils;
import com.mosioj.ideescadeaux.utils.database.NoRowsException;

@WebServlet("/public/reinitialiser_mot_de_passe")
public class ReinitialisationMotDePasse extends IdeesCadeauxGetAndPostServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 5998641192324526001L;
	public static final String VIEW_PAGE_URL = "/public/reinitialiser_mot_de_passe.jsp";
	public static final String SUCCES_PAGE_URL = "/public/reinitialiser_mot_de_passe_succes.jsp";

	private static final Logger logger = LogManager.getLogger(ReinitialisationMotDePasse.class);

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
		logger.info(MessageFormat.format("Demande de réinitialisation avec les emails suivants: {0}, {1}.", email1, email2));

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

		int userId;
		try {
			userId = model.users.getId(email1);
		} catch (NoRowsException e) {
			// L'email n'existe pas. On affiche la page de base pour éviter plus d'info aux pirates.
			ideesKDoGET(request, response);
			return;
		}

		int token = new Random().nextInt();
		UserChangePwdRequest changePwdRequest = new UserChangePwdRequest();
		changePwdRequest.deleteAssociation(userId);
		changePwdRequest.createNewRequest(userId, token);

		EmailSender.sendEmailReinitializationPwd(email1, userId, token);

		request.setAttribute("email", email1);
		RootingsUtils.rootToPage(SUCCES_PAGE_URL, request, response);
	}

}
