package com.mosioj.utils.database;

public class NoRowsException extends Exception {

	private static final long serialVersionUID = -1540821301172762945L;

	public NoRowsException() {
		super("No row retrieved");
	}

}
