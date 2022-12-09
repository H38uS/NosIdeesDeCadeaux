package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.CompteInteractions;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.IdeaLogic;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.validators.ValidatorBuilder;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@WebServlet("/protected/service/enregistrement_mon_compte")
public class ServiceEnregistrementMonCompte extends ServicePost<AllAccessToPostAndGet> {

    private static final Logger logger = LogManager.getLogger(ServiceEnregistrementMonCompte.class);

    /** Avatar's file path */
    private static final File FILE_PATH;

    public ServiceEnregistrementMonCompte() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        String message = "Le formulaire n'a pas le bon format.";
        ServiceResponse<?> ans = ServiceResponse.ko(message, thisOne);
        if (ServletFileUpload.isMultipartContent(request)) {

            Map<String, String> parameters = ParametersUtils.readMultiFormParameters(request, FILE_PATH);
            List<String> errors = processSave(FILE_PATH, parameters);
            if (errors == null || errors.isEmpty()) {
                request.setAttribute("connected_user", thisOne);
                request.getSession().setAttribute("connected_user", thisOne);
                ans = ServiceResponse.ok(thisOne, thisOne);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("<ul>");
                for (String error : errors) {
                    sb.append("<li>").append(error).append("</li>");
                }
                sb.append("</ul>");
                message = sb.toString();
                ans = ServiceResponse.ko(message, thisOne);
            }
        }

        buildResponse(response, ans);
    }

    public List<String> processSave(File filePath, Map<String, String> parameters) {

        CompteInteractions ci = new CompteInteractions();
        String info = parameters.get("modif_info_gen");
        List<String> errors = null;

        if ("true".equals(info)) {

            String email = StringEscapeUtils.escapeHtml4(parameters.get("email").trim());
            String name = StringEscapeUtils.escapeHtml4(parameters.get("name").trim());

            errors = ci.checkEmail(ci.getValidatorEmail(email), thisOne.id, true);

            String birthday = parameters.get("birthday");
            if (!birthday.isEmpty()) {
                logger.debug(MessageFormat.format("Date de naissance: {0}", birthday));
                errors.addAll(ValidatorBuilder.getFemValidator(birthday, "date d'anniversaire")
                                              .checkDateFormat()
                                              .build()
                                              .getErrors());
            }

            String newPwd = parameters.get("new_password").trim();
            String confPwd = parameters.get("conf_password").trim();

            if (!newPwd.isEmpty()) {
                List<String> pwdErrors1 = ci.getValidatorPwd(newPwd).getErrors();
                List<String> pwdErrors2 = ci.getValidatorPwd(confPwd).getErrors();
                if (!newPwd.equals(confPwd)) {
                    errors.add("Les deux mots de passe entrés ne correspondent pas.");
                }
                errors.addAll(pwdErrors1);
                errors.addAll(pwdErrors2);
            }

            thisOne.email = email;
            thisOne.name = name;
            MyDateFormatViewer.getAsDate(birthday).ifPresent(thisOne::setBirthday);

            String image = parameters.get("image");
            String old = parameters.get("old_picture");
            if (StringUtils.isBlank(image) || "null".equals(image)) {
                if (old != null && !old.equals("undefined")) {
                    image = old;
                } else {
                    image = null;
                }
            } else {
                // Modification de l'image
                // On supprime la précédente
                if (!"default.png".equals(old)) {
                    IdeaLogic.removeUploadedImage(filePath, old);
                }
                logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
            }
            thisOne.avatar = image;

            if (errors.isEmpty()) {
                logger.debug(MessageFormat.format("Updating user {0}. Email: {1}, name: {2}", thisOne, email, name));
                if (!newPwd.isEmpty()) {
                    String digested = CompteInteractions.hashPwd(newPwd);
                    thisOne.setPassword(digested);
                }
                HibernateUtil.update(thisOne);
            }
        }
        return errors;
    }

    static {
        FILE_PATH = new File(ParametersUtils.getWorkDir(), "uploaded_pictures/avatars");
        logger.info(MessageFormat.format("Setting file path to: {0}", FILE_PATH.getAbsolutePath()));
        if (!FILE_PATH.exists() && !FILE_PATH.mkdirs()) {
            logger.warn("Fail to create " + FILE_PATH);
        }
    }
}
