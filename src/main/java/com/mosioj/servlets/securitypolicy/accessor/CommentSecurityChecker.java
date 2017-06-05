package com.mosioj.servlets.securitypolicy.accessor;

import com.mosioj.model.Comment;

/**
 * Defines a class that aims to validate a comment parameter from a security point of view.
 * 
 * @author Jordan Mosio
 */
public interface CommentSecurityChecker {

	/**
	 * 
	 * @return The object if all validity checks passed, or null.
	 */
	public Comment getComment();
}
