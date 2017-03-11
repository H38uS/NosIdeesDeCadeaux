package com.mosioj.servlets.controllers.compte;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.notifications.instance.NotifNoIdea;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.viewhelper.EmptyFilter;
import com.mosioj.viewhelper.LoginHelper;

import nl.captcha.Captcha;

@WebServlet("/creation_compte")
public class CreationCompte extends DefaultCompte {

	public static final String SUCCES_URL = "/public/succes_creation.jsp";
	public static final String FORM_URL = "/public/creation_compte.jsp";

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
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Nothing to do
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);

		// Récupération des paramètres
		String pwd = ParametersUtils.readIt(request, "pwd");
		String email = ParametersUtils.readAndEscape(request, "email").trim();
		String name = ParametersUtils.readAndEscape(request, "pseudo").trim();

		// Validation des paramètres
		List<String> pwdErrors = checkPwd(getValidatorPwd(pwd));
		request.setAttribute("pwd_errors", pwdErrors);

		List<String> emailErrors = checkEmail(getValidatorEmail(email), -1); // The user does not exist yet
		request.setAttribute("email_errors", emailErrors);

		request.setCharacterEncoding("UTF-8"); // Do this so we can capture non-Latin chars
		String answer = request.getParameter("answer");
		boolean captchaOk = captcha.isCorrect(answer);
		if (!captchaOk) {
			request.setAttribute("captcha_errors", "Le texte entré ne correspond pas.");
		}

		// Password hash
		StringBuffer hashPwd = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(pwd.getBytes());
			byte[] digest = md.digest();
			for (byte b : digest) {
				hashPwd.append(String.format("%02x", b & 0xff));
			}
		} catch (NoSuchAlgorithmException e) {
			pwdErrors.add("Echec du chiffrement du mot de passe. Erreur: " + e.getMessage());
		}

		// Retour au formulaire si un paramètre est incorrect
		if (!pwdErrors.isEmpty() || !emailErrors.isEmpty() || !captchaOk) {
			RootingsUtils.rootToPage(FORM_URL, request, response);
			return;
		}

		// Les paramètres sont ok, on s'occupe de la requête
		try {
			users.addNewPersonne(email, hashPwd.toString(), name);
			session.invalidate();
			request.login(email, pwd);
			request.setAttribute("user", email);
			new LoginHelper().doFilter(request, response, new EmptyFilter());
			notif.addNotification(ParametersUtils.getUserId(request), new NotifNoIdea());
			RootingsUtils.rootToPage(SUCCES_URL, request, response);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
	}
}
