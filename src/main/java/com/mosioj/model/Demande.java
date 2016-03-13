package com.mosioj.model;

public class Demande {

	private final int joinerId;
	private final String joinerName;
	
	public Demande(int pJoinerId, String pJoinerName) {
		joinerId = pJoinerId;
		joinerName = pJoinerName;
	}
	
	public int getId() {
		return joinerId;
	}
	
	public String getName() {
		return joinerName;
	}
}