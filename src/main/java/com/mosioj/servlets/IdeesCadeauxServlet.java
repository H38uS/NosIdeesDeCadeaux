package com.mosioj.servlets;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Comment;
import com.mosioj.model.Idee;
import com.mosioj.model.table.Categories;
import com.mosioj.model.table.Comments;
import com.mosioj.model.table.GroupIdea;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.Priorites;
import com.mosioj.model.table.UserParameters;
import com.mosioj.model.table.UserRelationRequests;
import com.mosioj.model.table.UserRelations;
import com.mosioj.model.table.UserRelationsSuggestion;
import com.mosioj.model.table.Users;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.servlets.securitypolicy.accessor.CommentSecurityChecker;
import com.mosioj.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.database.DataSourceIdKDo;

import nl.captcha.Captcha;

/**
 * An intermediate servlet for test purpose. Increase the visibility of tested method.
 * 
 * @author Jordan Mosio
 *
 */
@SuppressWarnings("serial")
public abstract class IdeesCadeauxServlet extends HttpServlet {

	// TODO : vérifier que l'on redirige bien vers le site quand on est dans une frame etc => vérifier l'URL

	// TODO : réserver une sous partie de l'idée (genre moi je prends le tome XX)
	// TODO : choisir les pseudos de ses relations
	// TODO : bootstrap pour le CSS ??
	// TODO : externaliser les requêtes SQL et les tester ? Au moins les grosses ??
	// FIXME : 5 ZCompléter le gdoc avec les modifications faites

	// FIXME : 2 pouvoir réinitialiser le mot de pase

	// TODO : pouvoir créer des groupes d'utilisateurs pour les trouver plus facilement
	// TODO : notification quand un anniversaire approche

	// TODO : pouvoir ajouter des surprises
	// TODO : controle parental
	// TODO : auto logout en javascript

	// TODO : catcher quand la session a expiré, pour faire une joli page
	// TODO : configurer le nombre de jour pour le rappel d'anniversaire

	// TODO : notification quand on envoie une demande d'amis

	// TODO : faire un libellé plus joli pour les notifications dans MonCompte
	// TODO : bouton pour dire "mes idées sont à jour" ie on met à jour la date de modification

	private static final int MAX_WIDTH = 150;
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATETIME_DISPLAY_FORMAT = "dd MMM yyyy à HH:mm:ss";
	private static final List<String> sessionNamesToKeep = new ArrayList<String>();

	// Maximum 10M
	private static final int MAX_MEM_SIZE = 1024 * 1024 * 10;

	private static final Logger logger = LogManager.getLogger(IdeesCadeauxServlet.class);

	/**
	 * L'interface vers la table USER_RELATIONS.
	 */
	protected static UserRelations userRelations = new UserRelations();

	/**
	 * Interface vers la table USER_RELATION_REQUESTS.
	 */
	protected UserRelationRequests userRelationRequests;

	/**
	 * Interface vers la table USERS.
	 */
	protected Users users;

	/**
	 * The connection to use for parameters.
	 */
	protected DataSourceIdKDo validatorConnection;

	/**
	 * The connections to the IDEES table.
	 */
	protected static Idees idees = new Idees();

	/**
	 * The connections to the CATEGORIES table.
	 */
	protected Categories categories;

	/**
	 * The connections to the PRIORITIES table.
	 */
	protected Priorites priorities;

	/**
	 * The connections to the NOTIFICATION table.
	 */
	protected static Notifications notif = new Notifications();

	/**
	 * The connections to the GROUP_IDEA and GROUP_IDEA_CONTENT tables.
	 */
	protected GroupIdea groupForIdea;

	/**
	 * The connections to the USER_RELATIONS_SUGGESTION table.
	 */
	protected UserRelationsSuggestion userRelationsSuggestion;

	/**
	 * The connections to the COMMENTS table.
	 */
	protected static Comments comments = new Comments();

	/**
	 * The connections to the USER_PARAMETERS table.
	 */
	protected UserParameters userParameters = new UserParameters();

	/**
	 * The security policy defining whether we can interact with the parameters, etc.
	 */
	private final SecurityPolicy policy;
	protected Map<String, String> parameters;

	/**
	 * Class constructor.
	 * 
	 * @param policy The security policy defining whether we can interact with the parameters, etc.
	 */
	public IdeesCadeauxServlet(SecurityPolicy policy) {
		userRelationRequests = new UserRelationRequests();
		validatorConnection = new DataSourceIdKDo();
		users = new Users();
		categories = new Categories();
		priorities = new Priorites();
		groupForIdea = new GroupIdea();
		userRelationsSuggestion = new UserRelationsSuggestion();
		this.policy = policy;
	}

	/**
	 * 
	 * @return The idea being checked.
	 */
	protected Idee getIdeeFromSecurityChecks() {
		if (policy instanceof IdeaSecurityChecker) {
			return ((IdeaSecurityChecker) policy).getIdea();
		}
		return null;
	}

	/**
	 * 
	 * @return The commnet being checked.
	 */
	protected Comment getCommnetFromSecurityChecks() {
		if (policy instanceof CommentSecurityChecker) {
			return ((CommentSecurityChecker) policy).getComment();
		}
		return null;
	}

	/**
	 * For test purposes.
	 * 
	 * @param manager
	 */
	public void setNotificationManager(Notifications manager) {
		notif = manager;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pUserRelations
	 */
	public void setUserRelations(UserRelations pUserRelations) {
		userRelations = pUserRelations;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pUserRelationRequests
	 */
	public void setUserRelationRequests(UserRelationRequests pUserRelationRequests) {
		userRelationRequests = pUserRelationRequests;
	}

	/**
	 * For test purposes.
	 * 
	 * @param manager
	 */
	public void setValidatorConnection(DataSourceIdKDo manager) {
		validatorConnection = manager;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pUsers
	 */
	public void setUsers(Users pUsers) {
		users = pUsers;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pIdees
	 */
	public void setIdees(Idees pIdees) {
		idees = pIdees;
	}

	/**
	 * Internal class for GET processing, post security checks.
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (!policy.isGetRequestAllowed()) {
			super.doGet(req, resp);
			return;
		}

		try {

			if (!policy.hasRightToInteractInGetRequest(req, resp)) {

				int userId;
				try {
					userId = ParametersUtils.getUserId(req);
				} catch (Exception e) {
					userId = -1;
				}

				req.setAttribute("error_message", policy.getLastReason());
				logger.warn(MessageFormat.format(	"Inapropriate GET access from user {0} on {1}. Reason: {2}",
													userId,
													req.getRequestURL(),
													policy.getLastReason()));
				RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", req, resp);
				return;
			}

			// Converting session parameters to attributes
			HttpSession session = req.getSession();
			Enumeration<String> names = session.getAttributeNames();

			while (names.hasMoreElements()) {

				String name = names.nextElement();
				if (sessionNamesToKeep.contains(name)) {
					continue;
				}

				Object value = session.getAttribute(name);
				req.setAttribute(name, value);
				session.removeAttribute(name);
			}

			// Security has passed, perform the logic
			ideesKDoGET(req, resp);
		} catch (

		SQLException e) {
			// Default error management
			RootingsUtils.rootToGenericSQLError(e, req, resp);
		}
	};

	/**
	 * Internal class for POST processing, post security checks.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (!policy.isGetRequestAllowed()) {
			super.doGet(request, response);
			return;
		}

		try {

			if (!policy.hasRightToInteractInPostRequest(request, response)) {
				request.setAttribute("error_message", policy.getLastReason());
				logger.warn(MessageFormat.format(	"Inapropriate POST access from user {0} on {1}. Reason: {2}",
													ParametersUtils.getUserId(request),
													request.getRequestURL(),
													policy.getLastReason()));
				RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", request, response);
				return;
			}

			// Security has passed, perform the logic
			ideesKDoPOST(request, response);

		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
	}

	public void setCat(Categories cat) {
		categories = cat;
	}

	public void setPrio(Priorites prio) {
		priorities = prio;
	}

	protected java.sql.Date getAsDate(String date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		Date parsed;
		try {
			parsed = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		java.sql.Date sql = new java.sql.Date(parsed.getTime());
		return sql;
	}

	/**
	 * 
	 * @param originalImage
	 * @param type
	 * @return
	 */
	protected BufferedImage resizeImage(BufferedImage originalImage, int type, int maxWidth, int maxHeight) {

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		int newWidth = width > maxWidth ? maxWidth : width;
		int newHeight = (newWidth * height) / width;

		if (newHeight > maxHeight) {
			newWidth = (maxHeight * newWidth) / newHeight;
			newHeight = maxHeight;
		}

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}

	protected void readMultiFormParameters(HttpServletRequest request, File filePath) throws ServletException {

		parameters = new HashMap<String, String>();

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
					String fileName = fi.getName() == null ? "" : new String(fi.getName().getBytes("ISO-8859-1"), "UTF-8");
					if (!fileName.trim().isEmpty() && image.isEmpty()) {

						fileName = StringEscapeUtils.escapeHtml4(fileName);
						Random r = new Random();
						int id = r.nextInt();
						int maxSize = 30;
						if (fileName.length() > maxSize) {
							fileName = fileName.substring(0, maxSize - 4) + "_" + id + fileName.substring(fileName.length() - 4);
						} else {
							fileName = fileName.substring(0, fileName.length() - 4) + "_" + id
									+ fileName.substring(fileName.length() - 4);
						}
						image = fileName;
						File file = new File(filePath, "large/" + fileName);
						logger.debug("Uploading file : " + file);
						fi.write(file);

						// Creation de la vignette
						BufferedImage originalImage = ImageIO.read(file);
						int originalType = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

						BufferedImage resizeImageJpg = resizeImage(originalImage, originalType, MAX_WIDTH, MAX_WIDTH);
						ImageIO.write(resizeImageJpg, "png", new File(filePath, "small/" + fileName));

						if (originalImage.getWidth() > 1920 || originalImage.getHeight() > 1080) {
							resizeImageJpg = resizeImage(originalImage, originalType, 1920, 1080);
							ImageIO.write(resizeImageJpg, "png", new File(filePath, "large/" + fileName));
						}

						parameters.put("image", image);
					}
				} else {
					String val = fi.getString() == null ? "" : new String(fi.getString().getBytes("ISO-8859-1"), "UTF-8");
					parameters.put(fi.getFieldName(), StringEscapeUtils.escapeHtml4(val));
				}
			}
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

	protected void removeUploadedImage(File path, String image) {
		if (image != null && !image.isEmpty()) {
			File small = new File(path, "small/" + image);
			small.delete();
			File large = new File(path, "large/" + image);
			large.delete();
		}
	}

	/**
	 * 
	 * @param params Parameters received in this request.
	 * @param prefix The prefix to substract to get the id from the key.
	 * @return The list of selected integers.
	 */
	protected List<Integer> getSelectedChoices(Map<String, String[]> params, String prefix) {
		List<Integer> toBeAsked = new ArrayList<Integer>();
		for (String key : params.keySet()) {
			String[] values = params.get(key);
			if (key.startsWith(prefix) && values.length == 1 && "on".equals(values[0])) {
				String id = key.substring(prefix.length());
				try {
					toBeAsked.add(Integer.parseInt(id));
				} catch (NumberFormatException nfe) {
				}
			}
		}
		return toBeAsked;
	}

	static {
		sessionNamesToKeep.add("username");
		sessionNamesToKeep.add("userid");
		sessionNamesToKeep.add(Captcha.NAME);
	}

}
