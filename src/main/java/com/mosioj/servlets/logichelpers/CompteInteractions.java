package com.mosioj.servlets.logichelpers;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.model.table.Users;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.MyDateFormat;
import com.mosioj.utils.database.DataSourceIdKDo;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

public class CompteInteractions {

	private static final Logger logger = LogManager.getLogger(CompteInteractions.class);

	private Users users = new Users();
	private DataSourceIdKDo validatorConnection;

	public CompteInteractions() {
		validatorConnection = new DataSourceIdKDo();
	}

	/**
	 * Checks the validity of the pwd parameter.
	 * 
	 * @param validator
	 * @return The list of errors found.
	 */
	public List<String> checkPwd(ParameterValidator validator) {
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
	public List<String> checkEmail(ParameterValidator validator, int userId, boolean shouldExist) {
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

	public ParameterValidator getValidatorEmail(String email) {
		return ValidatorFactory.getNeutralValidator(email, "email");
	}

	public ParameterValidator getValidatorPwd(String pwd) {
		return ValidatorFactory.getMascValidator(pwd, "mot de passe");
	}

	public String hashPwd(String pwd, List<String> pwdErrors) {
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

	public java.sql.Date getAsDate(String date) {
		SimpleDateFormat format = new MyDateFormat(IdeesCadeauxServlet.DATE_FORMAT);
		Date parsed;
		try {
			parsed = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		java.sql.Date sql = new java.sql.Date(parsed.getTime());
		return sql;
	}

	public List<String> processSave(File filePath, Map<String, String> parameters, int userId) throws SQLException {

		String info = parameters.get("modif_info_gen");
		List<String> errors = null;

		if ("true".equals(info)) {

			String email = parameters.get("email").trim();
			String name = parameters.get("name").trim();

			errors = checkEmail(getValidatorEmail(email), userId, true);

			String birthday = parameters.get("birthday");
			if (!birthday.isEmpty()) {
				logger.debug(MessageFormat.format("Date de naissance: {0}", birthday));
				ParameterValidator val = ValidatorFactory.getFemValidator(birthday, "date d'anniversaire");
				val.checkDateFormat();
				errors.addAll(val.getErrors());
			}

			String newPwd = parameters.get("new_password").trim();
			String confPwd = parameters.get("conf_password").trim();

			if (newPwd != null && !newPwd.isEmpty()) {
				List<String> pwdErrors1 = checkPwd(getValidatorPwd(newPwd));
				List<String> pwdErrors2 = checkPwd(getValidatorPwd(confPwd));
				if (!newPwd.equals(confPwd)) {
					errors.add("Les deux mots de passe entrés ne correspondent pas.");
				}
				errors.addAll(pwdErrors1);
				errors.addAll(pwdErrors2);
			}

			User user = users.getUser(userId);
			user.email = email;
			user.name = name;
			user.birthday = getAsDate(birthday);

			String image = parameters.get("image");
			String old = parameters.get("old_picture");
			if (image == null || image.isEmpty()) {
				image = old;
			} else {
				// Modification de l'image
				// On supprime la précédente
				if (!"default.png".equals(old)) {
					IdeaInteractions helper = new IdeaInteractions();
					helper.removeUploadedImage(filePath, old);
				}
				logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
			}
			user.avatar = image;

			if (errors.isEmpty()) {
				users.update(user);
				if (!newPwd.isEmpty()) {
					String digested = hashPwd(newPwd, errors);
					users.updatePassword(userId, digested);
				}
			}

		}
		return errors;
	}

}
