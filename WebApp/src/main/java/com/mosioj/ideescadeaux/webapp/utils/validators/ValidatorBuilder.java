package com.mosioj.ideescadeaux.webapp.utils.validators;

public class ValidatorBuilder {

    /** The parameter validator */
    private final ParameterValidator validator;

    /**
     * @param validator The parameter validator.
     */
    private ValidatorBuilder(ParameterValidator validator) {
        this.validator = validator;
    }

    /**
     * @param value The parameter's value.
     * @param name  The parameter's name.
     * @return A new validator for a masculine parameter.
     */
    public static ValidatorBuilder getMascValidator(Object value, String name) {
        return new ValidatorBuilder(new ParameterValidator(value.toString(), name, "Le "));
    }

    /**
     * @param value The parameter's value.
     * @param name  The parameter's name.
     * @return A new validator for a feminine parameter.
     */
    public static ValidatorBuilder getFemValidator(Object value, String name) {
        return new ValidatorBuilder(new ParameterValidator(value.toString(), name, "La "));
    }

    /**
     * @param value The parameter's value.
     * @param name  The parameter's name.
     * @return A new validator for a parameter starting with a vowel.
     */
    public static ValidatorBuilder getNeutralValidator(Object value, String name) {
        return new ValidatorBuilder(new ParameterValidator(value.toString(), name, "L'"));
    }

    /**
     * @return The validator to use.
     */
    public ParameterValidator build() {
        return validator;
    }

    /**
     * Checks that the parameter is not empty.
     */
    public ValidatorBuilder checkEmpty() {
        validator.checkEmpty();
        return this;
    }

    /**
     * Checks that the parameter is an amount.
     */
    public ValidatorBuilder checkIfAmount() {
        validator.checkIfAmount();
        return this;
    }

    /**
     * Checks that the parameter's value is between those two values.
     *
     * @param min The minimum value allowed.
     * @param max The maximum value allowed.
     */
    public ValidatorBuilder checkDoubleAmount(double min, double max) {
        validator.checkDoubleAmount(min, max);
        return this;
    }

    /**
     * Checks that the parameter's value is greater than this minimum.
     *
     * @param min The minimum allowed value.
     */
    public ValidatorBuilder checkIntegerGreaterThan(int min) {
        validator.checkIntegerGreaterThan(min);
        return this;
    }

    /**
     * Validates the email format.
     */
    public ValidatorBuilder checkIsEmailValid() {
        validator.checkIsEmailValid();
        return this;
    }

    /**
     * Checks that the parameter size is in the given range.
     *
     * @param min The minimum allowed.
     * @param max The maximum allowed.
     */
    public ValidatorBuilder checkSize(int min, int max) {
        validator.checkSize(min, max);
        return this;
    }

    /**
     * Checks that parameter is an integer.
     */
    public ValidatorBuilder checkIfInteger() {
        validator.checkIfInteger();
        return this;
    }

    /**
     * Make sure the provided value complies with the expected date format.
     */
    public ValidatorBuilder checkDateFormat() {
        validator.checkDateFormat();
        return this;
    }
}
