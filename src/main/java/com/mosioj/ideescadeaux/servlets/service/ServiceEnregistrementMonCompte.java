package com.mosioj.ideescadeaux.servlets.service;

import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.logichelpers.CompteInteractions;
import com.mosioj.ideescadeaux.servlets.logichelpers.IdeaInteractions;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.date.MyDateFormatViewer;
import com.mosioj.ideescadeaux.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.utils.validators.ValidatorFactory;

@WebServlet("/protected/service/enregistrement_mon_compte")
public class ServiceEnregistrementMonCompte extends IdeesCadeauxPostServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = -3371121559895996016L;
    private static final Logger logger = LogManager.getLogger(ServiceEnregistrementMonCompte.class);

    private static File filePath;

    public ServiceEnregistrementMonCompte() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        String message = "Le formulaire n'a pas le bon format.";
        ServiceResponse ans = ServiceResponse.ko(message, true, isAdmin(request));
        if (ServletFileUpload.isMultipartContent(request)) {

            if (filePath == null) {
                filePath = new File(getServletContext().getInitParameter("work_dir"), "uploaded_pictures/avatars");
                logger.info(MessageFormat.format("Setting file path to: {0}", filePath.getAbsolutePath()));
                if (!filePath.exists() && !filePath.mkdirs()) {
                    logger.warn("Fail to create " + filePath);
                }
            }

            readMultiFormParameters(request, filePath);
            int userId = thisOne.id;

            List<String> errors = processSave(filePath, parameters, userId);
            if (errors == null || errors.isEmpty()) {
                User user = model.users.getUser(userId);
                request.setAttribute("connected_user", user);
                request.getSession().setAttribute("connected_user", user);
                ans = ServiceResponse.ok(user, isAdmin(request));
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("<ul>");
                for (String error : errors) {
                    sb.append("<li>").append(error).append("</li>");
                }
                sb.append("</ul>");
                message = sb.toString();
                ans = ServiceResponse.ko(message, true, isAdmin(request));
            }

        }

        buildResponse(response, ans);
        // FIXME : 0 faut tester tous les services modifiés
    }

    // La base est en UTC, il faut donc ne pas utiliser MySimpleDateFormat.
    // Ou alors, avec Hibernate et que la base soit en Europe/Paris.
    public java.sql.Date getAsDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(MyDateFormatViewer.DATE_FORMAT);
        Date parsed;
        try {
            parsed = format.parse(date);
        } catch (ParseException e) {
            return null;
        }
        return new java.sql.Date(parsed.getTime());
    }

    public List<String> processSave(File filePath, Map<String, String> parameters, int userId) throws SQLException {

        CompteInteractions ci = new CompteInteractions();
        String info = parameters.get("modif_info_gen");
        List<String> errors = null;

        if ("true".equals(info)) {

            String email = parameters.get("email").trim();
            String name = parameters.get("name").trim();

            errors = ci.checkEmail(ci.getValidatorEmail(email), userId, true);

            String birthday = parameters.get("birthday");
            if (!birthday.isEmpty()) {
                logger.debug(MessageFormat.format("Date de naissance: {0}", birthday));
                ParameterValidator val = ValidatorFactory.getFemValidator(birthday, "date d'anniversaire");
                val.checkDateFormat();
                errors.addAll(val.getErrors());
            }

            String newPwd = parameters.get("new_password").trim();
            String confPwd = parameters.get("conf_password").trim();

            if (!newPwd.isEmpty()) {
                List<String> pwdErrors1 = ci.checkPwd(ci.getValidatorPwd(newPwd));
                List<String> pwdErrors2 = ci.checkPwd(ci.getValidatorPwd(confPwd));
                if (!newPwd.equals(confPwd)) {
                    errors.add("Les deux mots de passe entrés ne correspondent pas.");
                }
                errors.addAll(pwdErrors1);
                errors.addAll(pwdErrors2);
            }

            User user = model.users.getUser(userId);
            user.email = email;
            user.name = name;
            user.birthday = getAsDate(birthday);

            String image = parameters.get("image");
            String old = parameters.get("old_picture");
            if (image == null || image.isEmpty() || "null".equals(image)) {
                if (old != null && !old.equals("undefined")) {
                    image = old;
                } else {
                    image = null;
                }
            } else {
                // Modification de l'image
                // On supprime la précédente
                if (!"default.png".equals(old)) {
                    IdeaInteractions helper = new IdeaInteractions();
                    helper.removeUploadedImage(filePath, old);
                }
                logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
            }
            user.avatar = image;

            if (errors.isEmpty()) {
                logger.debug(MessageFormat.format("Updating user {0}. Email: {1}, name: {2}", user, email, name));
                model.users.update(user);
                if (!newPwd.isEmpty()) {
                    String digested = ci.hashPwd(newPwd, errors);
                    model.users.updatePassword(userId, digested);
                }
            }

        }
        return errors;
    }

}
