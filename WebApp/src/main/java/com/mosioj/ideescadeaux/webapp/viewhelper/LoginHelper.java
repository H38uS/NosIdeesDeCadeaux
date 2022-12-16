package com.mosioj.ideescadeaux.webapp.viewhelper;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
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
import java.util.Optional;

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

    private HttpSession session;
    private HttpServletRequest request;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        this.request = (HttpServletRequest) request;
        this.session = this.request.getSession();

        logger.trace("Name: {} requesting URL: {}",
                     this.request.getRemoteUser(),
                     this.request.getRequestURL().toString());

        Optional.ofNullable(this.request.getRemoteUser()).ifPresent(this::processConnectedUser);
        setupFiles(request);
        request.setAttribute("is_admin", this.request.isUserInRole("ROLE_ADMIN"));

        chain.doFilter(request, response);
    }

    private void setupFiles(ServletRequest request) throws IOException {
        String workDir = ParametersUtils.getWorkDir();
        File work = new File(workDir);
        if (!work.exists() && !work.mkdirs()) {
            logger.error("Fail to create " + work + " directory.");
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
        request.setAttribute("work_dir", workDir);
        request.setAttribute("avatars", "protected/files/uploaded_pictures/avatars");
        request.setAttribute("ideas_pictures", "protected/files/uploaded_pictures/ideas");
    }

    private void processConnectedUser(String name) {

        // Storing the Id if not stored yet
        User user = (User) session.getAttribute("connected_user");
        if (user == null) {
            try {
                user = UsersRepository.getUser(name).orElseThrow(SQLException::new);
                // Storing the new one
                session.setAttribute("connected_user", user);
            } catch (SQLException e) {
                // Impossible: le nom existe forcément
                // Sait-on jamais, on le log
                logger.fatal(MessageFormat.format("L''utilisateur {0} est connecté mais n''a pas de compte...",
                                                  name));
            }
        }

        request.setAttribute("connected_user", user);

        // Child connection
        Object initial = session.getAttribute("initial_connected_user");
        Optional.ofNullable(initial).ifPresent(v -> request.setAttribute("initial_connected_user", v));

        // Mise à jour du nombre de notifications
        request.setAttribute("notif_count", NotificationsRepository.getUserNotificationCountWithChildren(user));
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
