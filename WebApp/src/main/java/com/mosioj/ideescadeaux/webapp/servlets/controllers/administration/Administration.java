package com.mosioj.ideescadeaux.webapp.servlets.controllers.administration;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/protected/administration/administration")
public class Administration extends IdeesCadeauxGetServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = 1944117196491457908L;
    private static final Logger logger = LogManager.getLogger(Administration.class);

    public static final String DISPATCH_URL = "/administration/administration.jsp";

    /**
     * Class constructor.
     */
    public Administration() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoGET(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, SQLException {

        logger.info("Getting administration page from user: " + thisOne);
        List<User> allUsers = UsersRepository.getAllUsers();
        request.setAttribute("users", allUsers);

        File logDir = new File(ParametersUtils.getWorkDir(), "logs");
        try {
            request.setAttribute("log_folder", logDir.getCanonicalPath());
            List<File> logFiles = Arrays.stream(Optional.ofNullable(logDir.listFiles()).orElse(new File[]{}))
                                        .filter(f -> f.getName().endsWith(".log"))
                                        .sorted(Comparator.reverseOrder())
                                        .limit(10)
                                        .collect(Collectors.toList());
            request.setAttribute("log_files", logFiles);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Une erreur est survenue...", e);
        }

        String memory = MessageFormat.format("Memory (free / total): ({0} Ko / {1} Ko). Max: {2} Ko.",
                                             Runtime.getRuntime().freeMemory() / 1024,
                                             Runtime.getRuntime().totalMemory() / 1024,
                                             Runtime.getRuntime().maxMemory() / 1024);
        request.setAttribute("memory", memory);

        checkForIdeasPictures(request);

        RootingsUtils.rootToPage(DISPATCH_URL, request, response);
    }

    protected void checkForIdeasPictures(HttpServletRequest request) {

        // Get all pictures
        List<String> imagesInIdeas = IdeesRepository.getAllImages();

        // And all files
        File smallFolder = new File(ParametersUtils.getWorkDir(), "uploaded_pictures/ideas/small");
        File largeFolder = new File(ParametersUtils.getWorkDir(), "uploaded_pictures/ideas/large");
        List<String> fileNamesInSmall = Arrays.stream(Optional.ofNullable(smallFolder.listFiles()).orElse(new File[]{}))
                                              .map(File::getName)
                                              .collect(Collectors.toList());
        List<String> fileNamesInLarge = Arrays.stream(Optional.ofNullable(largeFolder.listFiles()).orElse(new File[]{}))
                                              .map(File::getName)
                                              .collect(Collectors.toList());

        // Remove used pictures from small and large
        List<String> backup = new ArrayList<>(imagesInIdeas);
        imagesInIdeas.removeAll(fileNamesInSmall);
        imagesInIdeas.removeAll(fileNamesInLarge);

        // And if not all removed, the contrary
        fileNamesInSmall.removeAll(backup);
        fileNamesInLarge.removeAll(backup);

        Collections.sort(imagesInIdeas);
        Collections.sort(fileNamesInSmall);
        Collections.sort(fileNamesInLarge);

        // Sending info to the view
        request.setAttribute("missing_files_for_ideas", imagesInIdeas);
        request.setAttribute("extra_small_files", fileNamesInSmall);
        request.setAttribute("extra_large_files", fileNamesInLarge);
    }
}
