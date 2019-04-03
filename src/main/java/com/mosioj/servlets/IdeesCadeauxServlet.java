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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;

import com.mosioj.model.Idee;
import com.mosioj.model.User;
import com.mosioj.servlets.logichelpers.ModelAccessor;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.servlets.securitypolicy.accessor.IdeaSecurityChecker;
import com.mosioj.utils.Compteur;
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
public abstract class IdeesCadeauxServlet<P extends SecurityPolicy> extends HttpServlet {

	// FIXME : 99 mettre du bootstrap dans le site impulsion ?
	// FIXME : 99 et faire le lazy loading pour tout (genre les listes etc.) ??
	// TODO : voir la conf machine learning pour faire des trucs ??

	// TODO : voir pour utiliser hibernate ou jpa, et/ou spring mvc (restful pour plus tard)

	// TODO : notification followers quand on ajoute des idées, les modifie etc.

	// TODO : configurer le nombre de jour pour le rappel d'anniversaire

	// TODO : bouton pour dire "mes idées sont à jour" ie on met à jour la date de modification

	// TODO : pouvoir éditer un commentaire
	// TODO : mettre le focus sur le champs recherche dans mes listes
	// FIXME : 7 vue récapitulative avec toutes les idées qu'on a réservé, les groupes, les surprises.

	// TODO : faire du javascript pour les discussions

	// TODO : pouvoir se noter des idées en privé, puis les décaler en public

	// TODO : Si on change d'abonnement, elastic search ? Faut 2-4Go de RAM
	// FIXME : 92 remplir le gdoc + historiser la base de test

	// TODO : pouvoir modifier le niveau de log depuis l'administration
	// TODO : afficher le contenu des logs courant depuis l'administration ?

	// TODO : faire un seul repo git ? -- Faire que le task dépende d'impulsion ?
	// TODO : quand tout est fini: voir pour javax persistence et ce que ça peut apporter ?

	// TODO stocker l'objet User quand on se connecte
	// TODO bug avec l'IPhone, pas assez de padding sous le champs recherche
	// TODO pouvoir déréserver toutes ses idées

	// TODO vérifier en JS que le nombre de notification n'a pas bougé
	// TODO faire une appli androïd !!
	// TODO pour tous les noms faire une tooltip pour avoir l'email, la photo etc.

	private static final int MAX_WIDTH = 150;
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATETIME_DISPLAY_FORMAT = "d MMMM yyyy à HH'h'mm";

	// Maximum 10M
	private static final int MAX_MEM_SIZE = 1024 * 1024 * 10;

	private static final Logger logger = LogManager.getLogger(IdeesCadeauxServlet.class);

	// FIXME : 6 dans les questions, faire une couleur différente si c'est le owner qui répond

	// FIXME : 6 pouvoir inviter des gens via email dans ajouter amis si on ne les trouve pas
	// FIXME : 8 pouvoir réserver des surprises (groupe, réservation partielle, etc.)

	// FIXME : 99 en mode mobile, réduire le haut quand on clique sur le champs de recherche
	// FIXME : 99 vérifier régulièrement si y'a pas d'autres notif
	// FIXME : 99 ajouter les images des gens dans les recherches, en petit

	/**
	 * The security policy defining whether we can interact with the parameters, etc.
	 */
	protected final P policy;

	/**
	 * Interface to the DB model.
	 */
	protected final ModelAccessor model = new ModelAccessor();

	/**
	 * The connected user, or null if the user is not logged in.
	 */
	protected User thisOne = null; // TODO mettre un user fake ?

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
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

		Locale.setDefault(Locale.Category.FORMAT, Locale.FRANCE);
		fillConnectedUserIfPossible(request);
		policy.setConnectedUser(thisOne);

		if (!policy.isGetRequestAllowed()) {
			super.doGet(request, resp);
			return;
		}

		try {

			if (!policy.hasRightToInteractInGetRequest(request, resp) && !request.isUserInRole("ROLE_ADMIN")) {

				int userId;
				try {
					userId = thisOne.id;
				} catch (Exception e) {
					userId = -1;
				}

				request.setAttribute("error_message", policy.getLastReason());
				logger.warn(MessageFormat.format(	"Inapropriate GET access from user {0} on {1}. Reason: {2}",
													userId,
													request.getRequestURL(),
													policy.getLastReason()));
				RootingsUtils.rootToPage("/protected/erreur_parametre_ou_droit.jsp", request, resp);
				return;
			}

			String fullURL = request.getRequestURL().toString();
			model.notif.setURL(fullURL);

			device = (Device) request.getAttribute("device");

			if (request.getRemoteUser() != null) {
				try {
					// Mise à jour du nombre de notifications
					User thisUser = thisOne;
					final Compteur count = new Compteur(model.notif.getUserNotificationCount(thisUser.id));
					model.parentRelationship.getChildren(thisUser.id).forEach(c -> {
						try {
							count.add(model.notif.getUserNotificationCount(c.id));
						} catch (Exception e) {
							logger.warn(MessageFormat.format(	"Erreur lors de la récupération des notifications de l''enfant {0} ({1})",
																c.getName(),
																c.id));
						}
					});
					request.setAttribute("notif_count", count.getValue());

					// Ajout d'information sur l'idée du Security check
					if (policy instanceof IdeaSecurityChecker) {
						Idee idee = ((IdeaSecurityChecker) policy).getIdea();
						model.idees.fillAUserIdea(thisUser, idee, device);
					}

				} catch (Exception e) {
					// Osef
					logger.warn(MessageFormat.format("Erreur lors de la récupération du user Id et des notif...{0}", e.getMessage()));
					logger.warn(Arrays.toString(e.getStackTrace()));
				}
			}

			// Security has passed, perform the logic
			ideesKDoGET(request, resp);

		} catch (NotLoggedInException e) {
			// Redirection vers l'emplacement de login
			RootingsUtils.redirectToPage("/public/login.jsp", request, resp);
		} catch (SQLException e) {
			// Default error management
			RootingsUtils.rootToGenericSQLError(thisOne, e, request, resp);
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
		fillConnectedUserIfPossible(request);
		policy.setConnectedUser(thisOne);

		if (!policy.isGetRequestAllowed()) {
			super.doGet(request, response);
			return;
		}

		try {

			if (!policy.hasRightToInteractInPostRequest(request, response) && !request.isUserInRole("ROLE_ADMIN")) {

				int userId;
				try {
					userId = thisOne.id;
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
			model.notif.setURL(fullURL);

			device = (Device) request.getAttribute("device");

			if (request.getRemoteUser() != null) {
				try {
					// Mise à jour du nombre de notifications
					User thisUser = thisOne;
					final Compteur count = new Compteur(model.notif.getUserNotificationCount(thisUser.id));
					model.parentRelationship.getChildren(thisUser.id).forEach(c -> {
						try {
							count.add(model.notif.getUserNotificationCount(c.id));
						} catch (Exception e) {
							logger.warn(MessageFormat.format(	"Erreur lors de la récupération des notifications de l''enfant {0} ({1})",
																c.getName(),
																c.id));
						}
					});
					request.setAttribute("notif_count", count.getValue());

					// Ajout d'information sur l'idée du Security check
					if (policy instanceof IdeaSecurityChecker) {
						Idee idee = ((IdeaSecurityChecker) policy).getIdea();
						model.idees.fillAUserIdea(thisUser, idee, device);
					}

				} catch (Exception e) {
					// Osef
					logger.warn(MessageFormat.format("Erreur lors de la récupération du user Id et des notif...{0}", e.getMessage()));
					logger.warn(Arrays.toString(e.getStackTrace()));
				}
			}

			// Security has passed, perform the logic
			ideesKDoPOST(request, response);

		} catch (NotLoggedInException e) {
			// Redirection vers l'emplacement de login
			RootingsUtils.redirectToPage("/public/login.jsp", request, response);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(thisOne, e, request, response);
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

						if ("blob".equals(fileName)) {
							String inputFileName = parameters.get("fileName");
							fileName = inputFileName == null ? "IMG" : inputFileName;
						}
						image = Escaper.computeImageName(fileName);

						File largeFolder = new File(filePath, "large/");
						if (!largeFolder.exists()) {
							largeFolder.mkdirs();
						}
						File smallFolder = new File(filePath, "small/");
						if (!smallFolder.exists()) {
							smallFolder.mkdirs();
						}

						File tmpUploadedFile = new File(largeFolder, "TMP_" + image);
						logger.debug("Uploading file : " + tmpUploadedFile.getCanonicalPath());
						fi.write(tmpUploadedFile);
						logger.debug(MessageFormat.format("File size: {0} kos.", (tmpUploadedFile.length() / 1024)));
						logger.debug(MessageFormat.format(	"Memory (free / total): ( {0} Ko / {1} Ko ). Max: {2} Ko.",
															Runtime.getRuntime().freeMemory() / 1024,
															Runtime.getRuntime().totalMemory() / 1024,
															Runtime.getRuntime().maxMemory() / 1024));

						try {
							// Creation de la vignette
							BufferedImage originalImage = ImageIO.read(tmpUploadedFile);

							int originalType = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

							BufferedImage resizeImageJpg = resizeImage(originalImage, originalType, MAX_WIDTH, MAX_WIDTH);
							ImageIO.write(resizeImageJpg, "png", new File(smallFolder, image));

							// On l'écrit tout le temps pour avoir un PNG
							if (originalImage.getWidth() > 1920 || originalImage.getHeight() > 1080) {
								resizeImageJpg = resizeImage(originalImage, originalType, 1920, 1080);
							} else {
								resizeImageJpg = originalImage;
							}
							ImageIO.write(resizeImageJpg, "png", new File(largeFolder, image));
							
							// FIXME : 0 taille des images: mettre une hauteur fixe, et un <span> qui englobe avec une largeur ?
							// FIXME : 1 ajouter une taille mini ou une marge pour les icones dans mes listes

							logger.debug("Releasing the image resources...");
							originalImage.flush();

						} catch (OutOfMemoryError e) {
							logger.error(e);
							// On copy juste le fichier
							FileUtils.copyFile(tmpUploadedFile, new File(largeFolder, image));
							FileUtils.copyFile(tmpUploadedFile, new File(smallFolder, image));
						}

						tmpUploadedFile.delete();
						parameters.put("image", image);
					}
				} else {
					String val = fi.getString() == null ? "" : new String(fi.getString().getBytes("ISO-8859-1"), "UTF-8");
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

	/**
	 * 
	 * @param request
	 * @param ideaId
	 * @return The idea from the DB, enriched with useful information.
	 * @throws SQLException
	 * @throws NotLoggedInException
	 */
	protected Idee getIdeaAndEnrichIt(HttpServletRequest request, int ideaId) throws SQLException, NotLoggedInException {
		Idee idee = model.idees.getIdeaWithoutEnrichment(ideaId);
		model.idees.fillAUserIdea(thisOne, idee, device);
		return idee;
	}

	/**
	 * If the user is connected, sets up the field.
	 * 
	 * @param request
	 */
	private void fillConnectedUserIfPossible(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object connectedUser = session.getAttribute("connected_user");
		if (connectedUser != null) {
			thisOne = (User) connectedUser;
		}
	}
}
