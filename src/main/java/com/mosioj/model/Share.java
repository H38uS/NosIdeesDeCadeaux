package com.mosioj.model;

public class Share {
	
	private final User user;
	private final int amount;
	
	public Share(User user, int amount) {
		this.user = user;
		this.amount = amount;
	}

	public User getUser() {
		return user;
	}

	public int getAmount() {
		return amount;
	}
	
}