package com.mosioj.ideescadeaux.webapp.servlets.logichelpers;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.webapp.utils.validators.ValidatorBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.MessageFormat;
import java.util.List;

public class CompteInteractions {

    /** Password encoder. */
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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

    /**
     * @param pwd The raw password text.
     * @return The password hashed.
     */
    public static String hashPwd(String pwd) {
        return encoder.encode(pwd);
    }
}
