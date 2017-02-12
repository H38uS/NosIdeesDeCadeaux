package com.mosioj.model;

import java.util.ArrayList;
import java.util.List;

public class IdeaGroup {

	private final int total;
	private final int id;
	private final List<Share> shares = new ArrayList<Share>();
	
	public IdeaGroup(int id, int total) {
		this.id = id;
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public int getId() {
		return id;
	}

	public List<Share> getShares() {
		return shares;
	}

	public void addUser(int userId, int amount) {
		shares.add(new Share(userId, amount));
	}
	
}
