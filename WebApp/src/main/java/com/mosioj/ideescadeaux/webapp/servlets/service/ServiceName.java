package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.NameServicePolicy;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.NameAnswer;
import com.mosioj.ideescadeaux.webapp.utils.GsonFactory;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/protected/service/name_resolver")
public class ServiceName extends ServiceGet<NameServicePolicy> {

    private static final Logger logger = LogManager.getLogger(ServiceName.class);
    private static final long serialVersionUID = 9147880158497428623L;
    public static final String NAME_OR_EMAIL = "term";
    public static final String OF_USER_ID = "userId";

    public ServiceName() {
        super(new NameServicePolicy(OF_USER_ID));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        User current = policy.getRootNetwork();
        String param = ParametersUtils.readAndEscape(request, NAME_OR_EMAIL).toLowerCase();

        List<User> res = new ArrayList<>();
        int MAX = 5;
        if (current.getEmail().toLowerCase().contains(param)
            || (StringEscapeUtils.unescapeHtml4(current.getName().toLowerCase()).contains(param))
            || "moi".equalsIgnoreCase(param)) {
            res.add(current);
            MAX--;
        }

        res.addAll(UserRelationsRepository.getAllNamesOrEmailsInRelation(current.id, param, 0, MAX));

        // Building the JSON answer
        List<NameAnswer> users = res.stream().map(NameAnswer::new).collect(Collectors.toList());

        // Do not use ServiceResponse because specific format is needed for JQuery
        String content = GsonFactory.getIt().toJson(users);
        try {
            content = new String(content.getBytes(StandardCharsets.UTF_8), response.getCharacterEncoding());
            response.getOutputStream().print(content);
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
