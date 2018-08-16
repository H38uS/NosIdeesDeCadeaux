package com.mosioj.servlets;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;

import com.mosioj.model.Comment;
import com.mosioj.model.Idee;
import com.mosioj.model.Priorite;
import com.mosioj.model.User;
import com.mosioj.model.table.Categories;
import com.mosioj.model.table.Comments;
import com.mosioj.model.table.GroupIdea;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Notifications;
import com.mosioj.model.table.ParentRelationship;
import com.mosioj.model.table.Priorites;
import com.mosioj.model.table.Questions;
import com.mosioj.model.table.SousReservation;
import com.mosioj.model.table.UserParameters;
import com.mosioj.model.table.UserRelationRequests;
import com.mosioj.model.table.UserRelations;
import com.mosioj.model.table.UserRelationsSuggestion;
import com.mosioj.model.table.Users;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.servlets.securitypolicy.accessor.CommentSecurityChecker;
import com.mosioj.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.utils.NotLoggedInException;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.viewhelper.Escaper;

/**
 * An intermediate servlet for test purpose. Increase the visibility of tested method.
 * 
 * @author Jordan Mosio
 *
 */
@SuppressWarnings("serial")
public abstract class IdeesCadeauxServlet extends HttpServlet {

	// TODO : notification followers quand on ajoute des idées, les modifie etc.

	// TODO : configurer le nombre de jour pour le rappel d'anniversaire

	// TODO : bouton pour dire "mes idées sont à jour" ie on met à jour la date de modification

	// TODO : pouvoir éditer un commentaire
	// TODO : mettre le focus sur le champs recherche dans mes listes
	// TODO : vue récapitulative avec toutes les idées qu'on a réservé, les groupes, les surprises.
	// FIXME : 3 afficher une image si on a déjà fait une demande "est-ce à jour" + le faire dans le JS (i.e. recharger l'idée)
	// FIXME : 0 rejoindre un groupe en mode pc : moche !!
	// FIXME : 0 quand on annule, on ne revient pas sur le groupe ?

	// TODO : quand on affiche le rappel d'une idée, formater en mode classique + pouvoir intéragir ? -- genre groupe
	// etc.
	// TODO : pouvoir configurer le nombre de personnes sur une page dans "mes listes"

	// TODO : faire du javascript pour les discussions

	// TODO : pouvoir se noter des idées en privé, puis les décaler en public

	// TODO : pour les images, pouvoir faire un copier / coller ou un déplacer ici ?
	
	// FIXME : 4 voir pour elastic search :)
	// FIXME : 5 remplir le gdoc + historiser la base de test
	
	// TODO : pouvoir modifier le niveau de log depuis l'administration
	// TODO : afficher le contenu des logs courant depuis l'administration ?
	
	// FIXME : 3 racourcir les emails en vue portable ? Voir ceux de banque pop ?
	
	// TODO : faire un seul repo git ?
	// TODO : quand tout est fini: voir pour javax persistence et ce que ça peut apporter ?
	
	// TODO stocker l'objet User quand on se connecte
	
	// TODO : quand on clique sur compléter ma liste, n'afficher que le formulaire. Quand on veut sa liste, on la recherche !
	// TODO : ajouter l'année dans les dates ? Réservations, mise à jour...

	private static final int MAX_WIDTH = 150;
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATETIME_DISPLAY_FORMAT = "d MMMM à HH'h'mm";

	// Maximum 10M
	private static final int MAX_MEM_SIZE = 1024 * 1024 * 10;

	private static final Logger logger = LogManager.getLogger(IdeesCadeauxServlet.class);

	/**
	 * L'interface vers la table USER_RELATIONS. Static because it can be used in constructor for security checks.
	 */
	protected static UserRelations userRelations = new UserRelations();

	/**
	 * Interface vers la table USER_RELATION_REQUESTS.
	 */
	protected static UserRelationRequests userRelationRequests = new UserRelationRequests();;

	/**
	 * Interface vers la table USERS.
	 */
	protected Users users;

	/**
	 * The connections to the IDEES table. Static because it can be used in constructor for security checks.
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
	 * The connections to the NOTIFICATION table. Static because it can be used in constructor for security checks.
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
	 * The connections to the COMMENTS table. Static because it can be used in constructor for security checks.
	 */
	protected static Comments comments = new Comments();

	/**
	 * The connections to the QUESTIONS table. Static because it can be used in constructor for security checks.
	 */
	protected static Questions questions = new Questions();

	/**
	 * The connections to the USER_PARAMETERS table.
	 */
	protected UserParameters userParameters = new UserParameters();

	/**
	 * The connections to the SOUS_RESERVATION table.
	 */
	protected SousReservation sousReservation = new SousReservation();

	/**
	 * The connection to the PARENT_RELATIONSHIP table.
	 */
	protected static ParentRelationship parentRelationship = new ParentRelationship();

	/**
	 * The security policy defining whether we can interact with the parameters, etc.
	 */
	private final SecurityPolicy policy;
	protected Map<String, String> parameters;
	protected Device device;
	private File ideasPicturePath;

	/**
	 * Class constructor.
	 * 
	 * @param policy The security policy defining whether we can interact with the parameters, etc.
	 */
	public IdeesCadeauxServlet(SecurityPolicy policy) {
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
	 * @return The comment being checked.
	 */
	protected Comment getCommentFromSecurityChecks() {
		if (policy instanceof CommentSecurityChecker) {
			return ((CommentSecurityChecker) policy).getComment();
		}
		return null;
	}

	/**
	 * Internal class for GET processing, post security checks.
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Locale.setDefault(Locale.Category.FORMAT, Locale.FRANCE);

		if (!policy.isGetRequestAllowed()) {
			super.doGet(req, resp);
			return;
		}

		try {

			if (!policy.hasRightToInteractInGetRequest(req, resp) && !req.isUserInRole("ROLE_ADMIN")) {

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

			String fullURL = req.getRequestURL().toString();
			notif.setURL(fullURL);

			device = (Device) req.getAttribute("device");

			// Security has passed, perform the logic
			ideesKDoGET(req, resp);

		} catch (NotLoggedInException e) {
			// Redirection vers l'emplacement de login
			RootingsUtils.redirectToPage("/public/login.jsp", req, resp);
		} catch (SQLException e) {
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

		Locale.setDefault(Locale.Category.FORMAT, Locale.FRANCE);

		if (!policy.isGetRequestAllowed()) {
			super.doGet(request, response);
			return;
		}

		try {

			if (!policy.hasRightToInteractInPostRequest(request, response) && !request.isUserInRole("ROLE_ADMIN")) {

				int userId;
				try {
					userId = ParametersUtils.getUserId(request);
				} catch (Exception e) {
					userId = -1;
				}

				request.setAttribute("error_message", policy.getLastReason());
				logger.warn(MessageFormat.format(	"Inapropriate POST access from user {0} on {1}. Reason: {2}",
													userId,
													request.getRequestURL(),
													policy.getLastReason()));
				RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", request, response);
				return;
			}

			String fullURL = request.getRequestURL().toString();
			notif.setURL(fullURL);

			device = (Device) request.getAttribute("device");

			// Security has passed, perform the logic
			ideesKDoPOST(request, response);

		} catch (NotLoggedInException e) {
			// Redirection vers l'emplacement de login
			RootingsUtils.redirectToPage("/public/login.jsp", request, response);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
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
					logger.trace(MessageFormat.format("Character encoding: {0}", request.getCharacterEncoding()));
					String fileName = fi.getName() == null ? "" : fi.getName();
					logger.debug(MessageFormat.format("File name: {0}", fileName));
					if (!fileName.trim().isEmpty() && image.isEmpty()) {

						image = Escaper.computeImageName(fileName);
						image = image.replaceAll("'", "");
						image = image.replaceAll("[éêè]", "e");
						image = image.replaceAll("î", "i");
						image = image.replaceAll("ô", "o");
						image = image.replaceAll("[ùû]", "u");
						image = image.replaceAll("[àâ]", "a");
						image = StringEscapeUtils.escapeHtml4(image);

						File largeFolder = new File(filePath, "large/");
						if (!largeFolder.exists()) {
							largeFolder.mkdirs();
						}
						File smallFolder = new File(filePath, "small/");
						if (!smallFolder.exists()) {
							smallFolder.mkdirs();
						}

						File file = new File(largeFolder, image);
						logger.debug("Uploading file : " + file);
						fi.write(file);
						logger.debug(MessageFormat.format("File size: {0} kos.", (file.length() / 1024)));

						// Creation de la vignette
						BufferedImage originalImage = ImageIO.read(file);
						int originalType = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

						BufferedImage resizeImageJpg = resizeImage(originalImage, originalType, MAX_WIDTH, MAX_WIDTH);
						ImageIO.write(resizeImageJpg, "png", new File(smallFolder, image));

						if (originalImage.getWidth() > 1920 || originalImage.getHeight() > 1080) {
							resizeImageJpg = resizeImage(originalImage, originalType, 1920, 1080);
							ImageIO.write(resizeImageJpg, "png", new File(largeFolder, image));
						}

						parameters.put("image", image);
					}
				} else {
					String val = fi.getString() == null ? "" : new String(fi.getString().getBytes("ISO-8859-1"), "UTF-8");
					parameters.put(fi.getFieldName(), StringEscapeUtils.escapeHtml4(val));
				}
			}
		} catch (Exception e) {
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

	/**
	 * 
	 * @param request
	 * @param parameterName
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

	
	protected void fillAUserIdea(int userId, Idee idee) throws SQLException {

		idee.hasComment = comments.getNbComments(idee.getId()) > 0;
		idee.hasQuestion = questions.getNbQuestions(idee.getId()) > 0;
		
		User surpriseBy = idee.getSurpriseBy();
		if (surpriseBy != null) {
			if (surpriseBy.id == userId) {
				idee.displayClass = "booked_by_me_idea";
			} else {
				idee.displayClass = "booked_by_others_idea";
			}
		} else if (idee.isBooked()) {
			if (idee.getBookingOwner() != null) {
				if (idee.getBookingOwner().id == userId) {
					// Réservé par soit !
					idee.displayClass = "booked_by_me_idea";
				} else {
					// Réservé par un autre
					idee.displayClass = "booked_by_others_idea";
				}
			} else {
				// Réserver par un groupe
				idee.displayClass = "shared_booking_idea";
			}
		} else if (idee.isPartiallyBooked()) {
			idee.displayClass = "shared_booking_idea";
		}
		// Sinon, on laisse la class par défaut

		if (device.isMobile()) {
			Priorite priorite = idee.getPriorite();
			if (priorite != null && priorite.getImage() != null) {
				priorite.image = priorite.getImage().replaceAll("width=\"[0-9]+px\"", "width=\"80px\"");
			}
		}
	}

	protected void fillsUserIdeas(int userId, List<User> ids) throws SQLException {
		logger.trace("Getting all ideas for all users...");
		for (User user : ids) {
			List<Idee> ownerIdeas = idees.getOwnerIdeas(user.id);
			for (Idee idee : ownerIdeas) {
				fillAUserIdea(userId, idee);
			}
			user.addIdeas(ownerIdeas);
		}
	}
}
