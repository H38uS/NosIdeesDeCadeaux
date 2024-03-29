package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.booking.GroupIdeaRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.webapp.utils.validators.ValidatorBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/protected/service/group/create")
public class ServiceCreationGroup extends ServicePost<IdeaInteraction> {

    /** The parameter read to link the future group to the idea. */
    private static final String IDEE_FIELD_PARAMETER = "idee";

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(ServiceCreationGroup.class);

    /** Class constructor. */
    public ServiceCreationGroup() {
        super(new IdeaInteraction(IDEE_FIELD_PARAMETER));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        Idee idea = policy.getIdea();
        logger.debug("Create a new group for idea : {}", idea.getId());

        final String totalParam = ParametersUtils.getPOSTParameterAsString(request, "total");
        ParameterValidator valTot = ValidatorBuilder.getMascValidator(totalParam, "total")
                                                    .checkEmpty()
                                                    .checkIfAmount()
                                                    .checkIntegerGreaterThan(0)
                                                    .build();
        double total = ParametersUtils.readDouble(request, "total").orElse(0d);

        String amountString = ParametersUtils.getPOSTParameterAsString(request, "amount");
        ParameterValidator valAmount = ValidatorBuilder.getFemValidator(amountString, "participation")
                                                       .checkEmpty()
                                                       .checkIfAmount()
                                                       .build();
        valAmount.checkDoubleAmount(0, total);

        List<String> errors = valTot.getErrors();
        errors.addAll(valAmount.getErrors());
        if (!errors.isEmpty()) {
            buildResponse(response, ServiceResponse.ko(errors, thisOne));
            return;
        }

        final double amount = ParametersUtils.readDouble(request, "amount").orElse(0d);

        if (IdeesRepository.canBook(idea, thisOne)) {
            final IdeaGroup group = GroupIdeaRepository.createAGroup(total, amount, thisOne);
            IdeesRepository.bookByGroup(idea, group);
            logger.debug("Total: {}, amount: {}", total, amount);
            buildResponse(response, ServiceResponse.ok(group.getId(), thisOne));
        } else {
            buildResponse(response, ServiceResponse.ko("L'idée est déjà réservée.", thisOne));
        }
    }
}
