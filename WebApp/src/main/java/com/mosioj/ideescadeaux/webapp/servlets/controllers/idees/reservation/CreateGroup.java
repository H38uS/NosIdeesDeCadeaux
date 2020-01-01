package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.MesListes;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import com.mosioj.ideescadeaux.webapp.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.webapp.utils.validators.ValidatorFactory;

@WebServlet("/protected/create_a_group")
public class CreateGroup extends AbstractIdea<IdeaInteraction> {

    private static final long serialVersionUID = -1774633803227715931L;
    private static final Logger logger = LogManager.getLogger(CreateGroup.class);

    private static final String IDEE_FIELD_PARAMETER = "idee";
    public static final String VIEW_PAGE_URL = "/protected/create_a_group.jsp";

    /**
     * Class contructor
     */
    public CreateGroup() {
        super(new IdeaInteraction(IDEE_FIELD_PARAMETER));
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        req.setAttribute("idee", idea);
        RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        Idee idea = policy.getIdea();
        logger.debug("Create a new group for idea : " + idea.getId());

        ParameterValidator valTot = ValidatorFactory.getMascValidator(ParametersUtils.readIt(request, "total"),
                                                                      "total");
        valTot.checkEmpty();
        valTot.checkIfAmount();
        valTot.checkIntegerGreaterThan(0);

        String amountString = ParametersUtils.readIt(request, "amount");
        ParameterValidator valAmount = ValidatorFactory.getFemValidator(amountString, "participation");
        valAmount.checkEmpty();
        valAmount.checkIfAmount();

        Double total = ParametersUtils.readDouble(request, "total").orElse(0d);
        valAmount.checkDoubleAmount(0, total);

        List<String> errors = valTot.getErrors();
        errors.addAll(valAmount.getErrors());

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("idee", idea);
            request.setAttribute("total", total);
            request.setAttribute("amount", amountString);
            RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
            return;
        }

        Double amount = ParametersUtils.readDouble(request, "amount").orElse(0d);

        int userId = thisOne.id;
        Integer groupId = null;
        if (IdeesRepository.canBook(idea.getId(), userId)) {
            groupId = GroupIdeaRepository.createAGroup(total, amount, userId);
            IdeesRepository.bookByGroup(idea.getId(), groupId);
        }

        if (groupId == null) {
            RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
            return;
        }

        logger.debug(MessageFormat.format("Total: {0}, amount: {1}", total, amount));
        RootingsUtils.rootToPage(SuggestGroupIdea.VIEW_URL + "?" + SuggestGroupIdea.GROUP_ID_PARAM + "=" + groupId,
                                 request,
                                 response);
    }
}
