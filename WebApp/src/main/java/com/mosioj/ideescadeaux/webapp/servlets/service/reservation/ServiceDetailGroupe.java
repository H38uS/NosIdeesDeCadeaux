package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedIdeaGroup;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.BookingGroupInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/service/group/detail")
public class ServiceDetailGroupe extends ServiceGet<BookingGroupInteraction> {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(ServiceDetailGroupe.class);

    /** Group identifier parameter name. */
    public static final String GROUP_ID_PARAM = "groupid";

    /**
     * Class constructor.
     */
    public ServiceDetailGroupe() {
        super(new BookingGroupInteraction(GROUP_ID_PARAM));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {
        IdeaGroup group = policy.getGroupId();
        logger.debug("Getting details for idea group " + group + "...");
        buildResponse(response, ServiceResponse.ok(new DecoratedIdeaGroup(group, thisOne), isAdmin(request), thisOne));
    }
}
