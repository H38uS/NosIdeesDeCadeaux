package com.mosioj.ideescadeaux.webapp.utils.validators;

import com.mosioj.ideescadeaux.core.model.database.DataSourceIdKDo;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Permet à l'application de vérifier la pertinence des paramètres.
 *
 * @author Jordan Mosio
 */
public class ParameterValidator {

    private static final Logger logger = LogManager.getLogger(ParameterValidator.class);

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
     * @param pParameterName  The parameter name.
     * @param pArticle        L'article à utiliser devant le paramètre.
     */
    protected ParameterValidator(String pParameterValue, String pParameterName, String pArticle) {
        errors = new ArrayList<>();
        parameterName = pParameterName;
        article = pArticle;
        parameterValue = pParameterValue;
    }

    /**
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
            Integer.parseInt(parameterValue);
        } catch (NumberFormatException e) {
            errors.add(article + parameterName + " doit être un nombre.");
        }
    }

    public void checkIfAmount() {
        try {
            Double.parseDouble(parameterValue);
        } catch (NumberFormatException e) {
            errors.add(article + parameterName + " doit être un nombre.");
        }
    }

    public void checkDoubleAmount(double min, double max) {
        try {
            double val = Double.parseDouble(parameterValue);
            if (val < min) {
                errors.add(MessageFormat.format("{0}{1} doit être au moins égal à {2}.", article, parameterName, min));
            }
            if (val > max) {
                errors.add(MessageFormat.format("{0}{1} doit être plus petit que {2}.", article, parameterName, max));
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid value: " + parameterValue + ". Error: " + e.getMessage());
            errors.add("Valeur invalide.");
        }
    }

    public void checkIntegerGreaterThan(int min) {
        try {
            int val = Integer.parseInt(parameterValue);
            if (val < min) {
                errors.add(MessageFormat.format("{0}{1} doit être au moins égal à {2}.", article, parameterName, min));
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid value: " + parameterValue + ". Error: " + e.getMessage());
            errors.add("Valeur invalide.");
        }
    }

	/**
     * Checks that the parameter size is in the given range.
     *
     * @param min The minimum allowed.
     * @param max The maximum allowed.
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
     * Raises an error if the email already exists (1 or more occurrences).
     *
     * @param sqlQuery It must be a select, than returns 1 row and 1 integer column with the count of occurrence. The
     *                 query must also have exactly a string bind parameter for the parameter value.
     * @param db       The connection to use.
     */
    public void checkIsUnique(String sqlQuery, DataSourceIdKDo db) throws SQLException {
        int res = db.selectCountStar(sqlQuery, parameterValue);
        if (res > 0) {
            errors.add(article + parameterName + " existe déjà.");
        }
    }

    /**
     * Raises an error if the email does not exist (0 occurrence).
     *
     * @param sqlQuery It must be a select, than returns 1 row and 1 integer column with the count of occurrence. The
     *                 query must also have exactly a string bind parameter for the parameter value.
     * @param db       The connection to use.
     */
    public void checkExists(String sqlQuery, DataSourceIdKDo db) throws SQLException {
        int res = db.selectCountStar(sqlQuery, parameterValue);
        if (res == 0) {
            errors.add(article + parameterName + " n'existe pas.");
        }
    }

    public void checkDateFormat() {
        SimpleDateFormat sdf = new MyDateFormatViewer(MyDateFormatViewer.DATE_FORMAT);
        try {
            sdf.parse(parameterValue);
        } catch (ParseException e) {
            errors.add(MessageFormat.format("{0}{1} ({2}) ne correspond pas au format attendu, qui est : {3}.",
                                            article,
                                            parameterName,
                                            parameterValue,
                                            MyDateFormatViewer.DATE_FORMAT));
        }

    }
}
