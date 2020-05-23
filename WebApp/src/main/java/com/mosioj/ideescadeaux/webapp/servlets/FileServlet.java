package com.mosioj.ideescadeaux.webapp.servlets;

import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;

@WebServlet("/protected/files/*")
public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = -4352920790249582095L;
    private static final Logger logger = LogManager.getLogger(FileServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String filename = new String(request.getPathInfo().substring(1).getBytes(StandardCharsets.ISO_8859_1),
                                     StandardCharsets.UTF_8);
        logger.trace(MessageFormat.format("Requesting file: {0}, {1}", filename, request.getCharacterEncoding()));

        // Invalid file
        if (filename.trim().isEmpty()) {
            return;
        }

        // Admin files
        if (!filename.startsWith("uploaded_pictures")) {
            // Only for admins
            if (!request.isUserInRole("ROLE_ADMIN")) {
                return;
            }
        }

        // Avatar etc are only for connected users
        if (!request.isUserInRole("ROLE_USER")) {
            return;
        }

        File file = new File(ParametersUtils.getWorkDir(), filename);
        response.setHeader("Content-Type", getServletContext().getMimeType(filename));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        if (file.exists()) {
            Files.copy(file.toPath(), response.getOutputStream());
        } else {
            String fileName = file.toString();
            try {
                fileName = file.getCanonicalPath();
            } catch (Exception e) {
                logger.error(e);
                e.printStackTrace();
            }
            logger.error(MessageFormat.format("File {0} does not exist.", fileName));
        }
    }

}