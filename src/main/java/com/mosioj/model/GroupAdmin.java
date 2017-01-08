package com.mosioj.model;

public class GroupAdmin {
	
	private int groupId;
	private boolean isOwner;
	private User user;

	public GroupAdmin(int groupId, int userId, boolean isOwner) {
		this.groupId = groupId;
		user = new User(userId);
		this.isOwner = isOwner;
	}

	public int getGroupId() {
		return groupId;
	}

	public boolean getIsOwner() {
		return isOwner;
	}

	public User getUser() {
		return user;
	}

}
