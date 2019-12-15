package com.mosioj.ideescadeaux.servlets.logichelpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.List;

import com.mosioj.ideescadeaux.model.repositories.Users;
import com.mosioj.ideescadeaux.utils.database.DataSourceIdKDo;
import com.mosioj.ideescadeaux.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.utils.validators.ValidatorFactory;

public class CompteInteractions {

    private DataSourceIdKDo validatorConnection;

    public CompteInteractions() {
        validatorConnection = new DataSourceIdKDo();
    }

    /**
     * Checks the validity of the pwd parameter.
     *
     * @param validator The validator object.
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
	 * @param validator The validator object.
     * @param userId    The user id for who we are checking the email.
     * @return The list of errors found.
     */
    public List<String> checkEmail(ParameterValidator validator, int userId, boolean shouldExist) {
        validator.checkEmpty();
        validator.checkIsEmailValid();
        if (shouldExist) {
            if (userId < 0) {
                validator.checkExists(MessageFormat.format("select count(*) from {0} where email = ?",
                                                           Users.TABLE_NAME),
                                      validatorConnection);
            } else {
                validator.checkExists(MessageFormat.format("select count(*) from {0} where email = ? and id = {1}",
                                                           Users.TABLE_NAME,
                                                           userId),
                                      validatorConnection);
            }
        } else {
            validator.checkIsUnique(MessageFormat.format("select count(*) from {0} where email = ?",
                                                         Users.TABLE_NAME,
                                                         userId),
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
        StringBuilder hashPwd = new StringBuilder();
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
