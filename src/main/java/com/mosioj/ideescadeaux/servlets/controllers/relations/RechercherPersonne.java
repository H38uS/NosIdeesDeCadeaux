package com.mosioj.ideescadeaux.servlets.controllers.relations;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.servlets.controllers.AbstractListes;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.ParametersUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/protected/rechercher_personne")
public class RechercherPersonne extends AbstractListes<User, AllAccessToPostAndGet> {

    private static final long serialVersionUID = 9147880158497428623L;
    public static final String DEFAULT_FORM_URL = "/protected/rechercher_personne.jsp";
    public final String formUrl;

    /**
     * Class constructor.
     */
    public RechercherPersonne() {
        super(new AllAccessToPostAndGet());
        formUrl = DEFAULT_FORM_URL;
    }

    /**
     * Class constructor.
     */
    public RechercherPersonne(String dispatchURL) {
        super(new AllAccessToPostAndGet());
        formUrl = dispatchURL;
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
        String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
        boolean onlyNonFriend = "on".equals(val) || "true".equals(val);

        request.setAttribute("name", userNameOrEmail);
        request.setAttribute("onlyNonFriend", onlyNonFriend);

        super.ideesKDoGET(request, response);
    }

    @Override
    protected String getViewPageURL() {
        return formUrl;
    }

    @Override
    protected String getCallingURL() {
        return "protected/rechercher_personne";
    }

    @Override
    protected String getSpecificParameters(HttpServletRequest request) {
        return "&name=" +
               ParametersUtils.readAndEscape(request, "name").trim() +
               "&only_non_friend=" +
               ParametersUtils.readAndEscape(request, "only_non_friend").trim();
    }

    @Override
    protected int getTotalNumberOfRecords(HttpServletRequest request) throws SQLException {
        int userId = thisOne.id;
        String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
        String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
        boolean onlyNonFriend = "on".equals(val) || "true".equals(val);
        return UsersRepository.getTotalUsers(userNameOrEmail, userId, onlyNonFriend);
    }

    @Override
    protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest request) throws SQLException {

        int userId = thisOne.id;

        String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
        String val = ParametersUtils.readAndEscape(request, "only_non_friend").trim();
        boolean onlyNonFriend = "on".equals(val) || "true".equals(val);
        List<User> foundUsers = UsersRepository.getUsers(userNameOrEmail,
                                                         userId,
                                                         onlyNonFriend,
                                                         firstRow,
                                                         maxNumberOfResults);

        if (!onlyNonFriend) {
            for (User user : foundUsers) {
                user.isInMyNetwork = UserRelationsRepository.associationExists(user.id, userId);
            }
        }

        for (User user : foundUsers) {
            if (UserRelationRequestsRepository.associationExists(userId, user.id)) {
                user.freeComment = "Vous avez déjà envoyé une demande à " + user.getName();
            }
        }

        return foundUsers;
    }

}
