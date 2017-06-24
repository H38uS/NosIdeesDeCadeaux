package com.mosioj.servlets.controllers.idees;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

public abstract class AbstractIdea extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -1774633803227715931L;
	private static final Logger logger = LogManager.getLogger(AbstractIdea.class);

	public static final String IDEA_PICTURES_PATH = "/public/uploaded_pictures/ideas";

	protected List<String> errors = new ArrayList<String>();

	/**
	 * 
	 * @param policy The security policy defining whether we can interact with the parameters, etc.
	 */
	public AbstractIdea(SecurityPolicy policy) {
		super(policy);
	}

	protected void fillIdeaOrErrors(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		errors.clear();
		File filePath = new File(getServletContext().getRealPath(IDEA_PICTURES_PATH));

		// Reading parameters
		String text = "";
		String type = "";
		int priority = -1;

		// Parse the request to get file items.
		readMultiFormParameters(request, filePath);

		text = parameters.get("text");
		type = parameters.get("type");
		priority = Integer.parseInt(parameters.get("priority"));

		if (text.isEmpty() && type.isEmpty() && priority == -1) {
			logger.debug("All parameters are empty.");
			// We can assume we wanted to do a get
			ideesKDoGET(request, response);
			return;
		}

		ParameterValidator valText = ValidatorFactory.getMascValidator(text, "text");
		valText.checkEmpty();

		ParameterValidator valPrio = ValidatorFactory.getFemValidator(priority + "", "priorité");
		valPrio.checkEmpty();
		valPrio.checkIfInteger();

		errors.addAll(valText.getErrors());
		errors.addAll(valPrio.getErrors());
	}

}
