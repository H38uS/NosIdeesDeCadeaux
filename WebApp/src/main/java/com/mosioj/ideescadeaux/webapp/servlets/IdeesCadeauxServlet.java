package com.mosioj.ideescadeaux.webapp.servlets;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.utils.Compteur;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;

import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.ideescadeaux.core.utils.Escaper;

/**
 * An intermediate servlet for test purpose. Increase the visibility of tested method.
 *
 * @author Jordan Mosio
 */
@SuppressWarnings("serial")
public abstract class IdeesCadeauxServlet<P extends SecurityPolicy> extends HttpServlet {

    private static final int MAX_SIZE = 150;
    // Maximum 10M
    private static final int MAX_MEM_SIZE = 1024 * 1024 * 10;

    private static final Logger logger = LogManager.getLogger(IdeesCadeauxServlet.class);

    /**
     * The security policy defining whether we can interact with the parameters, etc.
     */
    protected final P policy;

    /**
     * The connected user, or null if the user is not logged in.
     */
    protected User thisOne = null;

    protected Map<String, String> parameters;
    protected Device device;
    private File ideasPicturePath;

    /**
     * Class constructor.
     *
     * @param policy The security policy defining whether we can interact with the parameters, etc.
     */
    public IdeesCadeauxServlet(P policy) {
        this.policy = policy;
    }

    /**
     * Internal class for GET processing, post security checks.
     *
     * @param request  The http request.
     * @param response The http response.
     */
    public abstract void ideesKDoGET(HttpServletRequest request,
                                     HttpServletResponse response) throws ServletException, SQLException, IOException;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

        Locale.setDefault(Locale.Category.FORMAT, Locale.FRANCE);
        fillConnectedUserIfPossible(request);
        policy.setConnectedUser(thisOne);
        policy.reset();

        if (!policy.hasRightToInteractInGetRequest(request, resp) && !isAdmin(request)) {

            int userId;
            try {
                userId = thisOne.id;
            } catch (Exception e) {
                userId = -1;
            }

            request.setAttribute("error_message", policy.getLastReason());
            logger.warn(MessageFormat.format("Inapropriate GET access from user {0} on {1}. Reason: {2}",
                                             userId,
                                             request.getRequestURL(),
                                             policy.getLastReason()));
            RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", request, resp);
            return;
        }

        device = (Device) request.getAttribute("device");

        if (request.getRemoteUser() != null) {
            try {
                // Mise à jour du nombre de notifications
                final Compteur count = new Compteur(NotificationsRepository.getUserNotificationCount(thisOne.id));
                ParentRelationshipRepository.getChildren(thisOne.id).forEach(c -> {
                    try {
                        count.add(NotificationsRepository.getUserNotificationCount(c.id));
                    } catch (Exception e) {
                        logger.warn(MessageFormat.format(
                                "Erreur lors de la récupération des notifications de l''enfant {0} ({1})",
                                c.getName(),
                                c.id));
                    }
                });
                request.setAttribute("notif_count", count.getValue());

                // Ajout d'information sur l'idée du Security check
                if (policy instanceof IdeaSecurityChecker) {
                    Idee idee = ((IdeaSecurityChecker) policy).getIdea();
                    IdeesRepository.fillAUserIdea(thisOne, idee, device.isMobile());
                }

            } catch (Exception e) {
                // Osef
                logger.warn(MessageFormat.format("Erreur lors de la récupération du user Id et des notif...{0}",
                                                 e.getMessage()));
                logger.warn(Arrays.toString(e.getStackTrace()));
            }
        }

        try {
            // Security has passed, perform the logic
            ideesKDoGET(request, resp);
        } catch (SQLException e) {
            // Default error management
            RootingsUtils.rootToGenericSQLError(thisOne, e, request, resp);
        }
    }

    /**
     * @param request The http request.
     * @return True if the user is an admin.
     */
    protected boolean isAdmin(HttpServletRequest request) {
        return request.isUserInRole("ROLE_ADMIN");
    }

    /**
     * Internal class for POST processing, post security checks.
     *
     * @param request  The http request.
     * @param response The http response.
     */
    public abstract void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException, IOException;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Locale.setDefault(Locale.Category.FORMAT, Locale.FRANCE);
        fillConnectedUserIfPossible(request);
        policy.setConnectedUser(thisOne);
        policy.reset();

        if (!policy.hasRightToInteractInPostRequest(request, response) && !isAdmin(request)) {

            int userId;
            try {
                userId = thisOne.id;
            } catch (Exception e) {
                userId = -1;
            }

            request.setAttribute("error_message", policy.getLastReason());
            logger.warn(MessageFormat.format("Inapropriate POST access from user {0} on {1}. Reason: {2}",
                                             userId,
                                             request.getRequestURL(),
                                             policy.getLastReason()));
            RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", request, response);
            return;
        }

        device = (Device) request.getAttribute("device");

        if (request.getRemoteUser() != null) {
            try {
                // Mise à jour du nombre de notifications
                User thisUser = thisOne;
                final Compteur count = new Compteur(NotificationsRepository.getUserNotificationCount(thisUser.id));
                ParentRelationshipRepository.getChildren(thisUser.id).forEach(c -> {
                    try {
                        count.add(NotificationsRepository.getUserNotificationCount(c.id));
                    } catch (Exception e) {
                        logger.warn(MessageFormat.format(
                                "Erreur lors de la récupération des notifications de l''enfant {0} ({1})",
                                c.getName(),
                                c.id));
                    }
                });
                request.setAttribute("notif_count", count.getValue());

                // Ajout d'information sur l'idée du Security check
                if (policy instanceof IdeaSecurityChecker) {
                    Idee idee = ((IdeaSecurityChecker) policy).getIdea();
                    IdeesRepository.fillAUserIdea(thisUser, idee, device.isMobile());
                }

            } catch (Exception e) {
                // Osef
                logger.warn(MessageFormat.format("Erreur lors de la récupération du user Id et des notif...{0}",
                                                 e.getMessage()));
                logger.warn(Arrays.toString(e.getStackTrace()));
            }
        }

        try {
            // Security has passed, perform the logic
            ideesKDoPOST(request, response);
        } catch (SQLException e) {
            RootingsUtils.rootToGenericSQLError(thisOne, e, request, response);
        }
    }

    /**
     * @param originalImage The picture received over the network.
     * @param type          The picture file extension.
     * @return A new picture resized for best rendering.
     */
    protected BufferedImage resizeImage(BufferedImage originalImage, int type, int maxWidth, int maxHeight) {

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int newWidth = Math.min(width, maxWidth);
        int newHeight = (newWidth * height) / width;

        if (newHeight > maxHeight) {
            newWidth = (maxHeight * newWidth) / newHeight;
            newHeight = maxHeight;
        }

        if (width == newWidth && height == newHeight) {
            // No resize needed
            return originalImage;
        }

        logger.debug("Resizing picture from (" + width + "x" + height + ") to (" + newWidth + "x" + newHeight + ")...");
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        logger.debug("Resize done!");

        return resizedImage;
    }

    protected void readMultiFormParameters(HttpServletRequest request, File filePath) throws ServletException {

        parameters = new HashMap<>();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(MAX_MEM_SIZE);
        factory.setRepository(filePath);

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(MAX_MEM_SIZE);

        String image = "";

        try {
            for (FileItem fi : upload.parseRequest(request)) {
                if (!fi.isFormField()) {
                    logger.trace(MessageFormat.format("Character encoding: {0}", request.getCharacterEncoding()));
                    String fileName = fi.getName() == null ? "" : fi.getName();
                    logger.debug(MessageFormat.format("File name: {0}", fileName));
                    if (!fileName.trim().isEmpty() && image.isEmpty()) {

                        if ("blob".equals(fileName)) {
                            String inputFileName = parameters.get("fileName");
                            fileName = inputFileName == null ? "IMG" : inputFileName;
                        }
                        image = Escaper.computeImageName(fileName);

                        File largeFolder = new File(filePath, "large/");
                        if (!largeFolder.exists()) {
                            if (!largeFolder.mkdirs()) {
                                logger.warn("Cannot create " + largeFolder);
                            }
                        }
                        File smallFolder = new File(filePath, "small/");
                        if (!smallFolder.exists()) {
                            if (!smallFolder.mkdirs()) {
                                logger.warn("Cannot create " + smallFolder);
                            }
                        }

                        File tmpUploadedFile = new File(largeFolder, "TMP_" + image);
                        logger.debug("Uploading file : " + tmpUploadedFile.getCanonicalPath());
                        fi.write(tmpUploadedFile);
                        logger.debug(MessageFormat.format("File size: {0} kos.", (tmpUploadedFile.length() / 1024)));
                        logger.debug(MessageFormat.format("Memory (free / total): ( {0} Ko / {1} Ko ). Max: {2} Ko.",
                                                          Runtime.getRuntime().freeMemory() / 1024,
                                                          Runtime.getRuntime().totalMemory() / 1024,
                                                          Runtime.getRuntime().maxMemory() / 1024));

                        try {
                            // Creation de la vignette
                            BufferedImage originalImage = ImageIO.read(tmpUploadedFile);

                            int originalType = originalImage.getType() ==
                                               0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

                            BufferedImage resizeImageJpg = resizeImage(originalImage, originalType, MAX_SIZE, MAX_SIZE);
                            ImageIO.write(resizeImageJpg, "png", new File(smallFolder, image));

                            // On l'écrit tout le temps pour avoir un PNG
                            if (originalImage.getWidth() > 1920 || originalImage.getHeight() > 1080) {
                                resizeImageJpg = resizeImage(originalImage, originalType, 1920, 1080);
                            } else {
                                resizeImageJpg = originalImage;
                            }
                            ImageIO.write(resizeImageJpg, "png", new File(largeFolder, image));
                            logger.debug("Releasing the image resources...");
                            originalImage.flush();

                        } catch (OutOfMemoryError e) {
                            logger.error(e);
                            // On copy juste le fichier
                            FileUtils.copyFile(tmpUploadedFile, new File(largeFolder, image));
                            FileUtils.copyFile(tmpUploadedFile, new File(smallFolder, image));
                        }

                        if (!tmpUploadedFile.delete()) {
                            logger.warn("Cannot delete " + tmpUploadedFile);
                        }
                        logger.debug(MessageFormat.format("Passing image parameter: {0}", image));
                        parameters.put("image", image);
                    }
                } else {
                    String val = fi.getString() == null ? "" : new String(fi.getString()
                                                                            .getBytes(StandardCharsets.ISO_8859_1),
                                                                          StandardCharsets.UTF_8);
                    parameters.put(fi.getFieldName(), StringEscapeUtils.escapeHtml4(val));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
    }

    protected File getIdeaPicturePath() {
        if (ideasPicturePath == null) {
            String workDir = getServletContext().getInitParameter("work_dir");
            logger.debug(MessageFormat.format("Initialisation du répertoire de travail à {0}", workDir));
            setIdeaPicturePath(new File(workDir, "uploaded_pictures/ideas"));
        }
        return ideasPicturePath;
    }

    public void setIdeaPicturePath(File file) {
        ideasPicturePath = file;
    }

    /**
     * @param params Parameters received in this request.
     * @param prefix The prefix to substract to get the id from the key.
     * @return The list of selected integers.
     */
    protected List<Integer> getSelectedChoices(Map<String, String[]> params, String prefix) {
        List<Integer> toBeAsked = new ArrayList<>();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            if (key.startsWith(prefix) && values.length == 1 && "on".equals(values[0])) {
                String id = key.substring(prefix.length());
                try {
                    toBeAsked.add(Integer.parseInt(id));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return toBeAsked;
    }

    /**
     * @param request       The http request.
     * @param parameterName The name of the parameter to read.
     * @return The String to pass to the database
     */
    protected String readNameOrEmail(HttpServletRequest request, String parameterName) {

        String nameOrEmail = ParametersUtils.readAndEscape(request, parameterName);
        logger.trace(MessageFormat.format("Receive:{0}", nameOrEmail));

        if (nameOrEmail == null || nameOrEmail.trim().isEmpty()) {
            return nameOrEmail;
        }

        int open = nameOrEmail.lastIndexOf("(");
        int close = nameOrEmail.lastIndexOf(")");
        if (open > 0 && close > 0 && open < close) {
            // Comes from some completion trick
            nameOrEmail = nameOrEmail.substring(open + 1, close);
        }

        logger.trace(MessageFormat.format("Returned:{0}", nameOrEmail.trim()));
        return nameOrEmail.trim();
    }

    /**
     * @param ideaId The idea's id.
     * @return The idea from the DB, enriched with useful information.
     */
    protected Idee getIdeaAndEnrichIt(int ideaId) throws SQLException {
        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(ideaId);
        IdeesRepository.fillAUserIdea(thisOne, idee, device.isMobile());
        return idee;
    }

    /**
     * If the user is connected, sets up the field.
     *
     * @param request The http request.
     */
    private void fillConnectedUserIfPossible(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object connectedUser = session.getAttribute("connected_user");
        if (connectedUser != null) {
            thisOne = (User) connectedUser;
        }
    }
}
