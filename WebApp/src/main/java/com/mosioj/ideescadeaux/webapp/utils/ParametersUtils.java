package com.mosioj.ideescadeaux.webapp.utils;

import com.mosioj.ideescadeaux.core.utils.Escaper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParametersUtils {

    private static final int MAX_SIZE = 150;

    /** Maximum 10M */
    private static final int MAX_MEM_SIZE = 1024 * 1024 * 10;

    /** Size of icon in mobile views */
    public static final int MOBILE_PICTURE_WIDTH = 42;

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(ParametersUtils.class);

    /** Application work directory where to store pictures and so on */
    private static final String WORK_DIR;

    /** Picture directory inside the working directory */
    private static final File IDEAS_PICTURE_PATH;

    /**
     * Attention: ne surtout pas utiliser dans les redirect post -> get.
     *
     * @param request The processing request.
     * @param name    The parameter name.
     * @return The parameter value, or empty string if not provided.
     */
    public static String readIt(HttpServletRequest request, String name) {
        String res = request.getParameter(name);
        logger.trace(MessageFormat.format("{0} is:{1}", name, res));
        return res == null ? "" : new String(res.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * @param request The http request.
     * @param name    The parameter name.
     * @return The parameter, as an integer. If it is not possible, returns null.
     */
    public static Optional<Integer> readInt(HttpServletRequest request, String name) {
        try {
            return Optional.of(Integer.parseInt(ParametersUtils.readIt(request, name)
                                                               .replaceAll("[  ]", "")
                                                               .replaceAll("%C2%A0", "")));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> readDouble(HttpServletRequest request, String name) {
        double param;
        try {
            param = Double.parseDouble(readIt(request, name).replaceAll("[  ]", "").replaceAll("%C2%A0", ""));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.of(param);
    }

    /**
     * Reads and escape HTML4 caracters.
     *
     * @param request The http request.
     * @param name    The name of the parameter.
     * @return The escaped string. Cannot be null.
     */
    public static String readAndEscape(HttpServletRequest request, String name) {
        return StringEscapeUtils.escapeHtml4(readIt(request, name));
    }

    /**
     * @param originalImage The picture received over the network.
     * @param type          The picture file extension.
     * @return A new picture resized for best rendering.
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int maxWidth, int maxHeight) {

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
        logger.trace("Resize done!");

        return resizedImage;
    }

    /**
     * @param request  The http request.
     * @param filePath The path where to store incoming pictures.
     * @return The parameters found as an hash map.
     * @throws SQLException If any exception occurs.
     */
    public static Map<String, String> readMultiFormParameters(HttpServletRequest request,
                                                              File filePath) throws SQLException {

        Map<String, String> parameters = new HashMap<>();

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
                    logger.debug(MessageFormat.format("Receiving file name: {0}", fileName));
                    if (!fileName.trim().isEmpty() && image.isEmpty()) {

                        if ("blob".equals(fileName)) {
                            String inputFileName = StringEscapeUtils.unescapeHtml4(parameters.get("fileName"));
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
                        logger.debug(MessageFormat.format("File size: {3} kos. - Memory (free / total): ( {0} Ko / {1} Ko ). Max: {2} Ko.",
                                                          Runtime.getRuntime().freeMemory() / 1024,
                                                          Runtime.getRuntime().totalMemory() / 1024,
                                                          Runtime.getRuntime().maxMemory() / 1024,
                                                          (tmpUploadedFile.length() / 1024)));

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
                            logger.trace("Releasing the image resources...");
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
                        logger.trace(MessageFormat.format("Passing image parameter: {0}", image));
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
            logger.error(e);
            throw new SQLException(e);
        }

        return parameters;
    }

    /**
     * @param request       The http request.
     * @param parameterName The name of the parameter to read.
     * @return The String to pass to the database
     */
    public static String readNameOrEmail(HttpServletRequest request, String parameterName) {

        String nameOrEmail = readAndEscape(request, parameterName);
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
     * @return The idea picture path.
     */
    public static File getIdeaPicturePath() {
        return IDEAS_PICTURE_PATH;
    }

    /**
     * @return The work directory.
     */
    public static String getWorkDir() {
        return WORK_DIR;
    }

    static {
        String tmp = ApplicationProperties.getProp().getProperty("work_dir");
        if (StringUtils.isBlank(tmp)) {
            tmp = "/temp";
        }
        WORK_DIR = tmp;
        logger.info("Work directory initialized to {}", WORK_DIR);

        IDEAS_PICTURE_PATH = new File(WORK_DIR, "uploaded_pictures/ideas");
        logger.info("Idea picture path directory initialized to {}", IDEAS_PICTURE_PATH);
    }

}
