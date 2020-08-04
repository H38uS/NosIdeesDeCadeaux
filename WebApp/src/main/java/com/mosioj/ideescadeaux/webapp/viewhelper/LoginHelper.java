package com.mosioj.ideescadeaux.webapp.viewhelper;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * Provides helper functions to the views.
 *
 * @author Jordan Mosio
 */
@WebFilter("/protected/*")
public class LoginHelper implements Filter {

    /**
     * Class logger.
     */
    private static final Logger logger = LogManager.getLogger(LoginHelper.class);

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        logger.trace("Do Filtering in helper...");

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession session = httpServletRequest.getSession();
        String name = httpServletRequest.getRemoteUser();
        logger.trace(MessageFormat.format("Name: {0} requesting URL: {1}",
                                          name,
                                          httpServletRequest.getRequestURL().toString()));
        if (name != null) {

            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            // Storing the Id if not stored yet
            User user = (User) session.getAttribute("connected_user");
            if (user == null) {
                try {
                    user = UsersRepository.getId(name).flatMap(UsersRepository::getUser).orElseThrow(SQLException::new);
                    // Storing the new one
                    session.setAttribute("connected_user", user);
                } catch (SQLException e) {
                    // Impossible: le nom existe forcément
                    // Sait-on jamais, on le log
                    logger.fatal(MessageFormat.format("L''utilisateur {0} est connecté mais n''a pas de compte...",
                                                      name));
                    e.printStackTrace();
                }
            }

            String workDir = ParametersUtils.getWorkDir();
            request.setAttribute("work_dir", workDir);
            request.setAttribute("connected_user", user);

            File work = new File(workDir);
            if (!work.exists()) {
                if (!work.mkdirs()) {
                    logger.error("Fail to create " + work + " directory.");
                }
            }
            File avatars = new File(work, "uploaded_pictures/avatars");
            if (!avatars.exists()) {
                // Création des sous dossiers
                if (!avatars.mkdirs()) {
                    logger.error("Fail to create " + work + " directory.");
                }
                FileUtils.copyDirectory(new File(session.getServletContext()
                                                        .getRealPath("/public/uploaded_pictures/avatars")),
                                        avatars);
            }
            request.setAttribute("avatars", "protected/files/uploaded_pictures/avatars");
            request.setAttribute("ideas_pictures", "protected/files/uploaded_pictures/ideas");

            // Child connection
            Object initial = session.getAttribute("initial_connected_user");
            if (initial != null) {
                request.setAttribute("initial_connected_user", initial);
            }

            request.setAttribute("is_admin", httpServletRequest.isUserInRole("ROLE_ADMIN"));
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Nothing to do
    }

    @Override
    public void destroy() {
        // Nothing to do
    }
}
