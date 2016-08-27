package com.mosioj.utils.validators;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;

import com.mosioj.utils.database.ConnectionIdKDo;

/**
 * Permet à l'application de vérifier la pertinence des paramètres.
 * 
 * @author Jordan Mosio
 *
 */
public class ParameterValidator {

	/**
	 * The list of errors found so far.
	 */
	private final List<String> errors;

	/**
	 * The parameter name.
	 */
	private final String parameterName;

	/**
	 * L'article à utiliser devant le paramètre.
	 */
	private final String article;

	/**
	 * The parameter value received.
	 */
	private final String parameterValue;

	/**
	 * Class constructor.
	 * 
	 * @param pParameterValue The parameter value received.
	 * @param pParameterName The parameter name.
	 * @param pArticle L'article à utiliser devant le paramètre.
	 */
	public ParameterValidator(String pParameterValue, String pParameterName, String pArticle) {
		errors = new ArrayList<String>();
		parameterName = pParameterName;
		article = pArticle;
		parameterValue = pParameterValue;
	}

	/**
	 * 
	 * @return The list of errors found so far.
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * Checks that the parameter is not empty.
	 */
	public void checkEmpty() {
		if (parameterValue.isEmpty()) {
			errors.add(article + parameterName + " ne peut pas être vide.");
		}
	}
	
	/**
	 * Checks that parameter is an integer.
	 */
	public void checkIfInteger() {
		try {
			Integer.valueOf(parameterValue);
		} catch (NumberFormatException e) {
			 errors.add(article + parameterName + " doit être un nombre.");
		}
	}

	/**
	 * Checks that the parameter size is in the given range.
	 * 
	 * @param min
	 * @param max
	 */
	public void checkSize(int min, int max) {
		if (parameterValue.length() < min) {
			errors.add(article + parameterName + " doit faire au moins " + min + " caractères.");
		}
		if (parameterValue.length() > max) {
			errors.add(article + parameterName + " doit faire moins de " + max + " caractères.");
		}
	}

	/**
	 * Validates the email format.
	 */
	public void checkIsEmailValid() {
		EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(parameterValue)) {
			errors.add(article + parameterName + " est incorrect.");
		}
	}

	/**
	 * 
	 * @param sqlQuery It must be a select, than returns 1 row and 1 integer column with the count of occurrence. The
	 *            query must also have exactly a string bind parameter for the parameter value.
	 * @param db The connection to use.
	 */
	public void checkIsUnique(String sqlQuery, ConnectionIdKDo db) {
		try {
			int res = db.selectInt(sqlQuery, parameterValue);
			if (res > 0) {
				errors.add(article + parameterName + " existe déjà.");
			}
		} catch (SQLException e) {
			errors.add("Erreur lors de la lecture en base, veuillez réessayer plus tard.");
		}
	}
}
