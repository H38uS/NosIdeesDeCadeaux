package com.mosioj.servlets.controllers.compte;

import java.util.List;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.validators.ParameterValidator;

public class DefaultCompte extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -101081965549681889L;
	public static final String VIEW_PAGE_URL = "/protected/mon_compte.jsp";


	/**
	 * Checks the validity of the pwd parameter.
	 * 
	 * @param validator
	 * @return The list of errors found.
	 */
	protected List<String> checkPwd(ParameterValidator validator) {
		validator.checkEmpty();
		validator.checkSize(8, 30);
		return validator.getErrors();
	}

	/**
	 * Checks the validity of the email parameter.
	 * 
	 * @param validator
	 * @param userId The user id for who we are checking the email.
	 * @return The list of errors found.
	 */
	protected List<String> checkEmail(ParameterValidator validator, int userId) {
		validator.checkEmpty();
		validator.checkIsEmailValid();
		validator.checkIsUnique("select count(*) from users where email = ? and id <> " + userId, validatorConnection);
		return validator.getErrors();
	}

	protected ParameterValidator getValidatorEmail(String email) {
		ParameterValidator validator;
		validator = new ParameterValidator(email, "email", "L'");
		return validator;
	}

	protected ParameterValidator getValidatorPwd(String pwd) {
		ParameterValidator validator = new ParameterValidator(pwd, "mot de passe", "Le ");
		return validator;
	}
}
