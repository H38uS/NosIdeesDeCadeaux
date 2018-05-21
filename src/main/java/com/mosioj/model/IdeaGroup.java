package com.mosioj.model;

import java.util.ArrayList;
import java.util.List;

public class IdeaGroup {

	private final double total;
	private final int id;
	private final List<Share> shares = new ArrayList<Share>();
	
	public IdeaGroup(int id, double d) {
		this.id = id;
		this.total = d;
	}

	public double getTotal() {
		return total;
	}
	
	public String getTotalAmount() {
		return String.format("%1$,.2f", total);
	}

	public int getId() {
		return id;
	}

	public List<Share> getShares() {
		return shares;
	}

	public void addUser(User user, double d) {
		shares.add(new Share(user, d));
	}
	
}
