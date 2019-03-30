package com.mosioj.servlets.controllers.compte;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.notifications.instance.NotifNoIdea;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.logichelpers.CompteInteractions;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.viewhelper.CaptchaHandler;
import com.mosioj.viewhelper.EmptyFilter;
import com.mosioj.viewhelper.LoginHelper;

@WebServlet("/public/creation_compte")
public class CreationCompte extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	public static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";
	public static final String SUCCES_URL = "/public/succes_creation.jsp";
	public static final String FORM_URL = "/public/creation_compte.jsp";
	private static final Logger logger = LogManager.getLogger(CreationCompte.class);

	/**
	 * Class contructor.
	 */
	public CreationCompte() {
		super(new AllAccessToPostAndGet());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -101081965549681889L;

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		RootingsUtils.rootToPage(FORM_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		HttpSession session = request.getSession();
		CompteInteractions helper = new CompteInteractions();

		// Récupération des paramètres
		String pwd = ParametersUtils.readIt(request, "pwd");
		String email = ParametersUtils.readAndEscape(request, "email").trim();
		String name = ParametersUtils.readAndEscape(request, "pseudo").trim();

		// Validation des paramètres
		List<String> pwdErrors = helper.checkPwd(helper.getValidatorPwd(pwd));
		request.setAttribute("pwd_errors", pwdErrors);

		List<String> emailErrors = helper.checkEmail(helper.getValidatorEmail(email), -1, false); // The user does not exist yet
		request.setAttribute("email_errors", emailErrors);

		try {
			// Do this so we can capture non-Latin chars
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new ServletException(e1.getMessage());
		}

		String captchaResponse = ParametersUtils.readIt(request, "g-recaptcha-response");
		String urlCalled = request.getRequestURL().toString();
		logger.debug(captchaResponse + " / " + request.getRequestURL());
		boolean captchaOk = urlCalled.startsWith(HTTP_LOCALHOST_8080) || CaptchaHandler.resolveIt(captchaResponse); 
		if (!captchaOk) {
			request.setAttribute("captcha_errors", "Erreur lors de la validation du Captcha.");
		}
		
		// Password hash
		String hashPwd = helper.hashPwd(pwd, pwdErrors);

		// Retour au formulaire si un paramètre est incorrect
		if (!pwdErrors.isEmpty() || !emailErrors.isEmpty() || !captchaOk) {
			RootingsUtils.rootToPage(FORM_URL, request, response);
			return;
		}

		// Les paramètres sont ok, on s'occupe de la requête
		name = name.trim().isEmpty() ? email : name;
		model.users.addNewPersonne(email, hashPwd, name);
		session.invalidate();
		request.login(email, pwd);
		request.setAttribute("user", name);
		try {
			new LoginHelper().doFilter(request, response, new EmptyFilter());
		} catch (IOException e) {
			throw new ServletException(e.getMessage());
		}

		model.notif.addNotification(thisOne.id, new NotifNoIdea());
		RootingsUtils.rootToPage(SUCCES_URL, request, response);
	}
}
