package com.mosioj.ideescadeaux.webapp.servlets.logichelpers;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.webapp.utils.validators.ValidatorBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.List;

public class CompteInteractions {

    private final DataSourceIdKDo validatorConnection;

    public CompteInteractions() {
        validatorConnection = new DataSourceIdKDo();
    }

    /**
     * Checks the validity of the email parameter.
     *
     * @param validator The validator object.
     * @param userId    The user id for who we are checking the email.
     * @return The list of errors found.
     */
    public List<String> checkEmail(ParameterValidator validator, int userId, boolean shouldExist) {
        if (shouldExist) {
            if (userId < 0) {
                validator.checkExists(MessageFormat.format("select count(*) from {0} where email = ?",
                                                           UsersRepository.TABLE_NAME),
                                      validatorConnection);
            } else {
                validator.checkExists(MessageFormat.format("select count(*) from {0} where email = ? and id = {1}",
                                                           UsersRepository.TABLE_NAME,
                                                           userId),
                                      validatorConnection);
            }
        } else {
            validator.checkIsUnique(MessageFormat.format("select count(*) from {0} where email = ?",
                                                         UsersRepository.TABLE_NAME,
                                                         userId),
                                    validatorConnection);
        }
        return validator.getErrors();
    }

    public ParameterValidator getValidatorEmail(String email) {
        return ValidatorBuilder.getNeutralValidator(email, "email").checkEmpty().checkIsEmailValid().build();
    }

    public ParameterValidator getValidatorPwd(String pwd) {
        return ValidatorBuilder.getMascValidator(pwd, "mot de passe").checkEmpty().checkSize(8, 30).build();
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
