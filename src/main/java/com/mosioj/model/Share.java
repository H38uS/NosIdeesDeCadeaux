package com.mosioj.model;

public class Share {
	
	private final User user;
	private final double amount;
	
	public Share(User user, double d) {
		this.user = user;
		this.amount = d;
	}

	public User getUser() {
		return user;
	}

	public double getAmount() {
		return amount;
	}
	
}