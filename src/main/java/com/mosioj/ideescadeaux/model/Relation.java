package com.mosioj.ideescadeaux.model;

public class Relation {
	
	private final User first;
	private final User second;
	public boolean secondIsInMyNetwork = false;

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
	
	/**
	 * 
	 * @return true if and only if "second" belongs to the same network as the connected user.
	 */
	public boolean getSecondIsInMyNetwork() {
		return secondIsInMyNetwork;
	}

	@Override
	public String toString() {
		return "Relation [first=" + first + ", second=" + second + ", secondIsInMyNetwork=" + secondIsInMyNetwork + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Relation))
			return false;
		Relation other = (Relation) obj;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}
	
}
