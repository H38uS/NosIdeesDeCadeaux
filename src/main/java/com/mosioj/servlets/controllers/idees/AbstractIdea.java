package com.mosioj.servlets.controllers.idees;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

public abstract class AbstractIdea extends IdeesCadeauxServlet {

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(AbstractIdea.class);
	private static final long serialVersionUID = -1774633803227715931L;

	// Maximum 10M
	private static final int MAX_MEM_SIZE = 1024 * 1024 * 10;

	protected List<String> errors = new ArrayList<String>();
	protected Map<String, String> parameters;

	/**
	 * 
	 * @param policy The security policy defining whether we can interact with the parameters, etc.
	 */
	public AbstractIdea(SecurityPolicy policy) {
		super(policy);
	}
	
	/**
	 * 
	 * @param originalImage
	 * @param type
	 * @return
	 */
	protected BufferedImage resizeImage(BufferedImage originalImage, int type, int maxWidth) {

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		int newWidth = width > maxWidth ? maxWidth : width;
		int newHeight = (newWidth * height) / width;

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

	protected void fillIdeaOrErrors(HttpServletRequest request, HttpServletResponse response, String getURL)
			throws Exception {

		File filePath = new File(getServletContext().getRealPath("/public/uploaded_pictures"));

		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(MAX_MEM_SIZE);
		factory.setRepository(filePath);

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(MAX_MEM_SIZE);

		// Reading parameters
		String text = "";
		String type = "";
		int priority = -1;
		String image = "";

		parameters = new HashMap<String, String>();

		// Parse the request to get file items.
		for (FileItem fi : upload.parseRequest(request)) {
			if (!fi.isFormField()) {
				String fileName = fi.getName() == null ? "" : new String(fi.getName().getBytes("ISO-8859-1"), "UTF-8");
				if (!fileName.trim().isEmpty() && image.isEmpty()) {

					Random r = new Random();
					int id = r.nextInt();
					int maxSize = 30;
					if (fileName.length() > maxSize) {
						fileName = fileName.substring(0, maxSize - 4) + "_" + id
								+ fileName.substring(fileName.length() - 4);
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
					int originalType = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
							: originalImage.getType();

					BufferedImage resizeImageJpg = resizeImage(originalImage, originalType, 400);
					ImageIO.write(resizeImageJpg, "png", new File(filePath, "small/" + fileName));

					if (originalImage.getWidth() > 1920) {
						resizeImageJpg = resizeImage(originalImage, originalType, 1920);
						ImageIO.write(resizeImageJpg, "png", new File(filePath, "large/" + fileName));
					}

					parameters.put("image", image);
				}
			} else {
				parameters.put(fi.getFieldName(), fi.getString() == null ? "" : new String(fi.getString().getBytes("ISO-8859-1"), "UTF-8"));
			}
		}

		text = parameters.get("text");
		type = parameters.get("type");
		priority = Integer.parseInt(parameters.get("priority"));

		if (text.isEmpty() && type.isEmpty() && priority == -1) {
			logger.debug("All parameters are empty.");
			// We can assume we wanted to do a get
			RootingsUtils.redirectToPage(getURL, request, response);
			return;
		}

		ParameterValidator valText = ValidatorFactory.getMascValidator(text, "text");
		valText.checkEmpty();

		ParameterValidator valPrio = ValidatorFactory.getFemValidator(priority + "", "priorité");
		valPrio.checkEmpty();
		valPrio.checkIfInteger();

		errors.addAll(valText.getErrors());
		errors.addAll(valPrio.getErrors());
	}

	protected void removeUploadedImage(String image) {
		if (image != null && !image.isEmpty()) {
			File path = new File(getServletContext().getRealPath("/public/uploaded_pictures"));
			File small = new File(path, "small/" + image);
			small.delete();
			File large = new File(path, "large/" + image);
			large.delete();
		}
	}

}
