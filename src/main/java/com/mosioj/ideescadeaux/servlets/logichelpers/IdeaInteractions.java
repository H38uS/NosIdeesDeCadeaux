package com.mosioj.ideescadeaux.servlets.logichelpers;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.repositories.GroupIdea;
import com.mosioj.ideescadeaux.model.repositories.Idees;
import com.mosioj.ideescadeaux.model.repositories.Notifications;
import com.mosioj.ideescadeaux.model.repositories.SousReservation;
import com.mosioj.ideescadeaux.model.repositories.Users;

public class IdeaInteractions {

	private static final Logger logger = LogManager.getLogger(IdeaInteractions.class);

	protected Notifications notif = new Notifications();
	protected GroupIdea groupForIdea = new GroupIdea();
	protected SousReservation sousReservation = new SousReservation();
	protected Idees idees = new Idees();
	protected Users users = new Users();

	public void removeUploadedImage(File path, String image) {
		if (image != null && !image.isEmpty()) {
			image = StringEscapeUtils.unescapeHtml4(image);
			String imageName = path.toString();
			try {
				imageName = path.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
				logger.warn(e.getMessage());
			}
			logger.debug(MessageFormat.format("Deleting pictures ({1}) in {0} folder...", imageName, image));
			File small = new File(path, "small/" + image);
			small.delete();
			File large = new File(path, "large/" + image);
			large.delete();
		}
	}
}