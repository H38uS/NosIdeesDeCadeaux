package com.mosioj.ideescadeaux.core.model.entities;

import java.sql.Time;
import java.util.List;

public class RelationSuggestion {

	public User suggestedBy;
	public User suggestedTo;
	public List<User> suggestions;
	private Time suggestedDate;

	public RelationSuggestion(User suggestedBy, User suggestedTo, List<User> suggestions, Time suggestedDate) {
		this.suggestedBy = suggestedBy;
		this.suggestedTo = suggestedTo;
		this.suggestions = suggestions;
		this.suggestedDate = suggestedDate;
	}

	public User getSuggestedBy() {
		return suggestedBy;
	}

	public User getSuggestedTo() {
		return suggestedTo;
	}

	public List<User> getSuggestions() {
		return suggestions;
	}

	public Time getSuggestedDate() {
		return suggestedDate;
	}

}
