package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.servlets.logichelpers.CompteInteractions;
import com.mosioj.ideescadeaux.servlets.logichelpers.IdeaInteractions;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.utils.date.MyDateFormatViewer;
import com.mosioj.ideescadeaux.utils.validators.ParameterValidator;
import com.mosioj.ideescadeaux.utils.validators.ValidatorFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        ServiceResponse<?> ans = ServiceResponse.ko(message, isAdmin(request));
        if (ServletFileUpload.isMultipartContent(request)) {

            File thePath = getFilePath(this);
            readMultiFormParameters(request, thePath);

            List<String> errors = processSave(thePath, parameters);
            if (errors == null || errors.isEmpty()) {
                request.setAttribute("connected_user", thisOne);
                request.getSession().setAttribute("connected_user", thisOne);
                ans = ServiceResponse.ok(thisOne, isAdmin(request));
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("<ul>");
                for (String error : errors) {
                    sb.append("<li>").append(error).append("</li>");
                }
                sb.append("</ul>");
                message = sb.toString();
                ans = ServiceResponse.ko(message, isAdmin(request));
            }
        }

        buildResponse(response, ans);
    }

    public static synchronized File getFilePath(ServiceEnregistrementMonCompte service) {
        if (filePath == null) {
            filePath = new File(service.getServletContext().getInitParameter("work_dir"), "uploaded_pictures/avatars");
            logger.info(MessageFormat.format("Setting file path to: {0}", filePath.getAbsolutePath()));
            if (!filePath.exists() && !filePath.mkdirs()) {
                logger.warn("Fail to create " + filePath);
            }
        }
        return filePath;
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

    public List<String> processSave(File filePath, Map<String, String> parameters) throws SQLException {

        CompteInteractions ci = new CompteInteractions();
        String info = parameters.get("modif_info_gen");
        List<String> errors = null;

        if ("true".equals(info)) {

            String email = parameters.get("email").trim();
            String name = parameters.get("name").trim();

            errors = ci.checkEmail(ci.getValidatorEmail(email), thisOne.id, true);

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

            thisOne.email = email;
            thisOne.name = name;
            thisOne.birthday = getAsDate(birthday);

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
                    IdeaInteractions helper = new IdeaInteractions();
                    helper.removeUploadedImage(filePath, old);
                }
                logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
            }
            thisOne.avatar = image;

            if (errors.isEmpty()) {
                logger.debug(MessageFormat.format("Updating user {0}. Email: {1}, name: {2}", thisOne, email, name));
                UsersRepository.update(thisOne);
                if (!newPwd.isEmpty()) {
                    String digested = ci.hashPwd(newPwd, errors);
                    UsersRepository.updatePassword(thisOne.id, digested);
                }
            }
        }
        return errors;
    }

}
