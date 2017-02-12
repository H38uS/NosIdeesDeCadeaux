package com.mosioj.model;

public class Share {
	
	private final User user;
	private final int amount;
	
	public Share(int userId, int amount) {
		user = new User(userId);
		this.amount = amount;
	}

	public User getUser() {
		return user;
	}

	public int getAmount() {
		return amount;
	}
	
}