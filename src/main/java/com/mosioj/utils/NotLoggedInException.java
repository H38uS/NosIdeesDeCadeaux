package com.mosioj.utils;

import javax.servlet.ServletException;

public class NotLoggedInException extends ServletException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1816403929559182769L;

	/**
	 * Class constructor.
	 * 
	 * @param message The exception message.
	 */
	public NotLoggedInException(String message) {
		super(message);
	}

}
