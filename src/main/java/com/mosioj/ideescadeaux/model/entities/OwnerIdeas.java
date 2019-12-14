package com.mosioj.ideescadeaux.model.entities;

import java.util.List;

import com.google.gson.annotations.Expose;

public class OwnerIdeas {

	@Expose
	private final User owner;
	
	@Expose
	private final List<Idee> ideas;
	
	public OwnerIdeas(User owner, List<Idee> ideas) {
		this.owner = owner;
		this.ideas = ideas;
	}

	/**
	 * Factory method.
	 * 
	 * @param owner The owner.
	 * @param ideas His list of ideas.
	 * @return The combined object.
	 */
	public static OwnerIdeas from(User owner, List<Idee> ideas) {
		return new OwnerIdeas(owner, ideas);
	}

	/**
	 * @return the owner
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * @return the ideas
	 */
	public List<Idee> getIdeas() {
		return ideas;
	}
}
