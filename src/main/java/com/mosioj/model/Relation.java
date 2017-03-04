package com.mosioj.model;

public class Relation {
	
	private final User first;
	private final User second;

	/**
	 * @param first
	 * @param second
	 * 
	 */
	public Relation(User first, User second) {
		this.first = first;
		this.second = second;
	}

	public User getFirst() {
		return first;
	}

	public User getSecond() {
		return second;
	}

}
