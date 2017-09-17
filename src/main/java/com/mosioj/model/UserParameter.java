package com.mosioj.model;

public class UserParameter {

	private int id;
	public int userId;
	public String parameterName;
	public String parameterValue;
	public String description;

	public UserParameter(int id, int userId, String parameterName, String parameterValue, String description) {
		this.id = id;
		this.userId = userId;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public String getParameterName() {
		return parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}
	
	public String getParameterDescription() {
		return description;
	}
}
