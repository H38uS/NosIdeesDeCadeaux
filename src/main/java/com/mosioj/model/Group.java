package com.mosioj.model;

/**
 * Handle group of users.
 * 
 * @author Jordan Mosio
 *
 */
public class Group {

	private final int id;
	private final String name;
	private final int nbMembers;
	private final String status;

	/**
	 * Internal constructor.
	 * 
	 * @param pId
	 * @param pName
	 * @param pNbMembers
	 * @param pStatus
	 */
	public Group(int pId, String pName, int pNbMembers, String pStatus) {
		id = pId;
		name = pName;
		nbMembers = pNbMembers;
		status = pStatus;
	}

	/**
	 * 
	 * @return This group id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return This group name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The number of members belonging to this group.
	 */
	public int getNbMembers() {
		return nbMembers;
	}

	/**
	 * 
	 * @return An optional group status.
	 */
	public String getStatus() {
		return status;
	}

}
