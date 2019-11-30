package com.mosioj.ideescadeaux.servlets.controllers.idees.modification;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.Idee;
import com.mosioj.ideescadeaux.model.User;
import com.mosioj.ideescadeaux.model.table.IsUpToDateQuestions;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.notifications.instance.NotifConfirmedUpToDate;
import com.mosioj.ideescadeaux.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.servlets.controllers.idees.AbstractIdea;
import com.mosioj.ideescadeaux.servlets.controllers.idees.MaListe;
import com.mosioj.ideescadeaux.servlets.logichelpers.IdeaInteractions;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaModification;
import com.mosioj.ideescadeaux.utils.RootingsUtils;

@WebServlet("/protected/modifier_idee")
public class ModifyIdea extends AbstractIdea<IdeaModification> {

	private static final Logger logger = LogManager.getLogger(ModifyIdea.class);
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String IDEE_ID_PARAM = "id";
	public static final String VIEW_PAGE_URL = "/protected/modify_idea.jsp";
	public static final String PROTECTED_MODIFIER_IDEE = "/protected/modifier_idee";

	/**
	 * Class constructor.
	 */
	public ModifyIdea() {
		super(new IdeaModification(IDEE_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Idee idea = policy.getIdea();

		request.setAttribute("types", model.categories.getCategories());
		request.setAttribute("priorites", model.priorities.getPriorities());
		request.setAttribute("idea", idea);
		request.setAttribute("from", getFrom(request, ""));

		Object errors = request.getSession().getAttribute("errors");
		if (errors != null) {
			request.setAttribute("errors", errors);
			request.getSession().removeAttribute("errors");
		}

		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException, IOException {

		Idee idea = policy.getIdea();

		// Check that we have a file upload request
		if (ServletFileUpload.isMultipartContent(request)) {

			fillIdeaOrErrors(request, response);

			if (!errors.isEmpty()) {
				request.getSession().setAttribute("errors", errors);
				StringBuilder sb = new StringBuilder();
				sb.append(PROTECTED_MODIFIER_IDEE);
				sb.append("?");
				sb.append(IDEE_ID_PARAM);
				sb.append("=");
				sb.append(idea.getId());
				sb.append("&");
				sb.append("from=");
				sb.append(getFrom(request, ""));
				RootingsUtils.redirectToPage(sb.toString(), request, response);
				return;
			} else {
				logger.info(MessageFormat.format(	"Modifying an idea [''{0}'' / ''{1}'' / ''{2}'']",
													parameters.get("text"),
													parameters.get("type"),
													parameters.get("priority")));

				String image = parameters.get("image");
				String old = parameters.get("old_picture");
				logger.debug("Image précédente: " + old + " / Nouvelle image: " + image);
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
						helper.removeUploadedImage(getIdeaPicturePath(), old);
					}
					logger.debug(MessageFormat.format("Updating image from {0} to {1}.", old, image));
				}

				model.idees.modifier(idea.getId(), parameters.get("text"), parameters.get("type"), parameters.get("priority"), image);
				User user = thisOne;

				// Ajout de notification aux amis si l'anniversaire approche
				addModificationNotification(user, policy.getIdea(), false);

				// Suppression des demandes si y'en avait
				new IsUpToDateQuestions().deleteAssociations(idea.getId());
				
				List<AbstractNotification> notifications = model.notif.getNotification(ParameterName.IDEA_ID, idea.getId());
				for (AbstractNotification notification : notifications) {
					if (notification instanceof NotifAskIfIsUpToDate) {
						NotifAskIfIsUpToDate isUpToDate = (NotifAskIfIsUpToDate) notification;
						model.notif.addNotification(isUpToDate.getUserIdParam(), new NotifConfirmedUpToDate(user, idea));
						model.notif.remove(notification.id);
					}
					if (notification instanceof NotifIdeaAddedByFriend) {
						model.notif.remove(notification.id);
					}
				}
			}

		}

		RootingsUtils.redirectToPage(getFrom(request, MaListe.PROTECTED_MA_LISTE + "?" + "id=" + idea.getId()), request, response);
	}

}
