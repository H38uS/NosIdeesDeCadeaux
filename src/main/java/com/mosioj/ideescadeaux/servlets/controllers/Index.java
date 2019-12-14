package com.mosioj.ideescadeaux.servlets.controllers;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.MessagesAccueil;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/index")
public class Index extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

	public static final int NB_DAYS_MAX_BEFORE_BIRTHDAY = 20;
	private static final long serialVersionUID = -8386214705432810179L;
	private static final String VIEW_URL = "/protected/index.jsp";

	public Index() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		User me = thisOne;
		req.setAttribute("no_birth_date_set", me.getBirthday() == null);

		// Birthday messages
		List<User> friends = model.userRelations.getCloseBirthday(thisOne, NB_DAYS_MAX_BEFORE_BIRTHDAY);
		Set<User> listIBookedSomething = new HashSet<User>();
		model.idees.getIdeasWhereIDoParticipateIn(thisOne).parallelStream().forEach(i -> listIBookedSomething.add(i.owner));
		friends.parallelStream().filter(f -> listIBookedSomething.contains(f)).forEach(f -> f.hasBookedOneOfItsIdeas = true);

		req.setAttribute("userBirthday", friends);
		if (!friends.isEmpty()) {
			req.setAttribute("birthdayMessage", MessagesAccueil.getOneBirthdayMessage());
		}

		// Christmas
		Calendar now = Calendar.getInstance();
		if (now.after(startOfNotification()) && now.before(thisYearChristmas())) {
			req.setAttribute("christmasMessage", MessagesAccueil.getOneChristmasMessage());
		} else {
			if (friends.isEmpty()) {
				req.setAttribute("nothingMessage", MessagesAccueil.getOneNothingMessage());
			}
		}

		RootingsUtils.rootToPage(VIEW_URL, req, resp);
	}

	/**
	 * 
	 * @return The date from which we start notifying people about Christmas
	 */
	private static Calendar startOfNotification() {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.DECEMBER, 1);
		return date;
	}

	/**
	 * 
	 * @return This year Chistmas.
	 */
	private static Calendar thisYearChristmas() {
		Calendar christmas = Calendar.getInstance();
		christmas.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.DECEMBER, 25);
		return christmas;
	}
}