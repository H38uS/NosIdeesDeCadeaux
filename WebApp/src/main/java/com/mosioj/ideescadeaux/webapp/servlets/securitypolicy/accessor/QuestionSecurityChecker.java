package com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor;

import com.mosioj.ideescadeaux.core.model.entities.text.Question;

/**
 * Defines a class that aims to validate a question parameter from a security point of view.
 *
 * @author Jordan Mosio
 */
public interface QuestionSecurityChecker {

    /**
     * @return The object if all validity checks passed, or null.
     */
    Question getQuestion();
}
