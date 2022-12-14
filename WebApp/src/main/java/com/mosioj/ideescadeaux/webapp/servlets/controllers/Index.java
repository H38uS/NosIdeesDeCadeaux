package com.mosioj.ideescadeaux.webapp.servlets.controllers;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.MessagesAccueilRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppUser;
import com.mosioj.ideescadeaux.webapp.repositories.IdeasWithInfoRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/protected/index")
public class Index extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    public static final int NB_DAYS_MAX_BEFORE_BIRTHDAY = 20;
    private static final String VIEW_URL = "/protected/index.jsp";

    public Index() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws SQLException {

        User me = thisOne;
        req.setAttribute("no_birth_date_set", me.getBirthday().isEmpty());

        // Birthday messages
        List<DecoratedWebAppUser> friends = UserRelationsRepository.getCloseBirthday(thisOne,
                                                                                     NB_DAYS_MAX_BEFORE_BIRTHDAY)
                                                                   .stream()
                                                                   .map(u -> new DecoratedWebAppUser(u, thisOne))
                                                                   .collect(Collectors.toList());

        req.setAttribute("userBirthday", friends);
        if (!friends.isEmpty()) {
            req.setAttribute("birthdayMessage", MessagesAccueilRepository.getOneBirthdayMessage());
        }

        // Christmas
        Calendar now = Calendar.getInstance();
        if (now.after(startOfNotification()) && now.before(thisYearChristmas())) {
            req.setAttribute("christmasMessage", MessagesAccueilRepository.getOneChristmasMessage());
        } else {
            if (friends.isEmpty()) {
                req.setAttribute("nothingMessage", MessagesAccueilRepository.getOneNothingMessage());
            }
        }

        // All ideas for which we do participate
        req.setAttribute("nb_of_reservations", IdeasWithInfoRepository.getIdeasWhereIDoParticipateIn(thisOne).size());

        RootingsUtils.rootToPage(VIEW_URL, req, resp);
    }

    /**
     * @return The date from which we start notifying people about Christmas
     */
    private static Calendar startOfNotification() {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.DECEMBER, 1);
        return date;
    }

    /**
     * @return This year Chistmas.
     */
    private static Calendar thisYearChristmas() {
        Calendar christmas = Calendar.getInstance();
        christmas.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.DECEMBER, 25);
        return christmas;
    }
}
