package com.mosioj.servlets.controllers.compte;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.List;

import com.mosioj.model.table.Users;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

public abstract class DefaultCompte extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -101081965549681889L;
	public static final String VIEW_PAGE_URL = "/protected/mon_compte.jsp";

	/**
	 * Class contructor.
	 * 
	 * @param policy The security policy defining whether we can interact with the parameters, etc.
	 */
	public DefaultCompte(SecurityPolicy policy) {
		super(policy);
	}

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
	protected List<String> checkEmail(ParameterValidator validator, int userId, boolean shouldExist) {
		validator.checkEmpty();
		validator.checkIsEmailValid();
		if (shouldExist) {
			if (userId < 0) {
				validator.checkExists(	MessageFormat.format("select count(*) from {0} where email = ?", Users.TABLE_NAME),
										validatorConnection);
			} else {
				validator.checkExists(	MessageFormat.format(	"select count(*) from {0} where email = ? and id = {1}",
																Users.TABLE_NAME,
																userId),
										validatorConnection);
			}
		} else {
			validator.checkIsUnique(MessageFormat.format("select count(*) from {0} where email = ?", Users.TABLE_NAME, userId),
									validatorConnection);
		}
		return validator.getErrors();
	}

	protected ParameterValidator getValidatorEmail(String email) {
		return ValidatorFactory.getNeutralValidator(email, "email");
	}

	protected ParameterValidator getValidatorPwd(String pwd) {
		return ValidatorFactory.getMascValidator(pwd, "mot de passe");
	}

	protected String hashPwd(String pwd, List<String> pwdErrors) {
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
		return hashPwd.toString();
	}
}
