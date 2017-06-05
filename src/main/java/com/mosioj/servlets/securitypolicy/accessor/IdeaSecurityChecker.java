package com.mosioj.servlets.securitypolicy.accessor;

import com.mosioj.model.Idee;

/**
 * Defines a class that aims to validate an idea parameter from a security point of view.
 * 
 * @author Jordan Mosio
 */
public interface IdeaSecurityChecker {

	/**
	 * 
	 * @return The object if all validity checks passed, or null.
	 */
	public Idee getIdea();
}
