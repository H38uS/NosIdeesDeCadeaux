package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.utils.ParametersUtils;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

/**
 * Do not use for the users' idea.
 *
 * @author Jordan Mosio
 */
@WebServlet("/protected/service/get_idea_of_friend")
public class GetIdeaOfFriendService extends IdeesCadeauxGetServlet<IdeaInteraction> {

    private static final long serialVersionUID = -3425240682690763149L;
    private static final Logger logger = LogManager.getLogger(GetIdeaOfFriendService.class);

    public static final String IDEA_ID_PARAM = "idee";
    private static final String FROM_PARAM = "from";
    public static final String VIEW_PAGE_URL = "/protected/service/get_idea_of_friend.jsp";

    public GetIdeaOfFriendService() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        Idee idee = policy.getIdea();
        String from = ParametersUtils.readIt(request, FROM_PARAM);
        if (from.startsWith("/"))
            from = from.substring(1);

        logger.debug(MessageFormat.format("Getting idea {0} from service call (from {1})...", idee.getId(), from));

        model.idees.fillAUserIdea(thisOne, idee, device);

        request.setAttribute("idee", idee);
        request.setAttribute("identic_call_back", from);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response); // FIXME refactor et utiliser du json
    }
}
