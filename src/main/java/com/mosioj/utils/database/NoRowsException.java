package com.mosioj.utils.database;

import java.sql.SQLException;

public class NoRowsException extends SQLException {

	private static final long serialVersionUID = -1540821301172762945L;

	public NoRowsException() {
		super("No row retrieved");
	}

}
