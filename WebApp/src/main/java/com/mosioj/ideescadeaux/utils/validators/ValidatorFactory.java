package com.mosioj.ideescadeaux.utils.validators;

public class ValidatorFactory {

	private ValidatorFactory() {
		// Forbidden
	}

	/**
	 * 
	 * @param value
	 * @param name
	 * @return A new validator for a masculine parameter.
	 */
	public static ParameterValidator getMascValidator(Object value, String name) {
		return new ParameterValidator(value.toString(), name, "Le ");
	}

	/**
	 * 
	 * @param value
	 * @param name
	 * @return A new validator for a feminine parameter.
	 */
	public static ParameterValidator getFemValidator(Object value, String name) {
		return new ParameterValidator(value.toString(), name, "La ");
	}

	/**
	 * 
	 * @param value
	 * @param name
	 * @return A new validator for a parameter starting with a vowel.
	 */
	public static ParameterValidator getNeutralValidator(Object value, String name) {
		return new ParameterValidator(value.toString(), name, "L'");
	}

}
