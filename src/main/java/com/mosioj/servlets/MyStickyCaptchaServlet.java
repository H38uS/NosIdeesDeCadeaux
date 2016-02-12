package com.mosioj.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.servlet.StickyCaptchaServlet;

/**
 * Builds a CAPTCHA and stores it in the session. This is intended to prevent
 * bots from simply reloading the page and getting new images until one is
 * generated which they can successfully parse. Removal of the session attribute
 * <code>CaptchaServletUtil.NAME</code> will force a new <code>Captcha</code> to
 * be added to the session. (Obviously, this is not a perfect solution as
 * session IDs can simply not be passed.)
 *
 * The size of the image is by default 200x50. This can be customized using the
 * <code>captcha-height</code> and <code>captcha-width</code> init parameters in
 * web.xml.
 *
 * By default the CAPTCHA will expire after 600000ms. This can be overridden
 * using the <code>ttl</code> init parameter, in milliseconds.
 *
 * An example showing all parameters:
 *
 * <pre>
 *    &lt;servlet&gt;
 *         &lt;servlet-name&gt;StickyCaptcha&lt;/servlet-name&gt;
 *         &lt;servlet-class&gt;nl.captcha.servlet.StickyCaptchaServlet&lt;/servlet-class&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;captcha-width&lt;/param-name&gt;
 *             &lt;param-value&gt;400&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;captcha-height&lt;/param-name&gt;
 *             &lt;param-value&gt;200&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;ttl&lt;/param-name&gt;
 *             &lt;param-value&gt;900000&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *       &lt;/servlet&gt;
 * </pre>
 *
 * Since the constructed image is a PNG the servlet mapping should be defined
 * something like this:
 *
 * <pre>
 *     &lt;servlet-mapping&gt;
 *         &lt;servlet-name&gt;StickyCaptcha&lt;/servlet-name&gt;
 *         &lt;url-pattern&gt;/stickyCaptcha.png&lt;/url-pattern&gt;
 *     &lt;/servlet-mapping&gt;</pre>
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 */
@SuppressWarnings("serial")
public class MyStickyCaptchaServlet extends StickyCaptchaServlet {

	private static int _width = 200;
	private static int _height = 50;

	private static long _ttl = 1000 * 60 * 10;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if (getInitParameter("captcha-height") != null) {
			_height = Integer.valueOf(getInitParameter("captcha-height"));
		}

		if (getInitParameter("captcha-width") != null) {
			_width = Integer.valueOf(getInitParameter("captcha-width"));
		}

		if (getInitParameter("ttl") != null) {
			_ttl = Long.valueOf(getInitParameter("ttl"));
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		Captcha captcha;

		if (session.getAttribute(Captcha.NAME) == null) {
			captcha = buildAndSetCaptcha(session);
		}

		captcha = (Captcha) session.getAttribute(Captcha.NAME);
		if (shouldExpire(captcha)) {
			captcha = buildAndSetCaptcha(session);
		}

		CaptchaServletUtil.writeImage(response, captcha.getImage());
	}

	private Captcha buildAndSetCaptcha(HttpSession session) {
		Captcha captcha = new Captcha.Builder(_width,
				_height).addText().gimp().addBorder().addNoise().addBackground().build();

		session.setAttribute(Captcha.NAME, captcha);
		return captcha;
	}

	/**
	 * Set the length of time the CAPTCHA will live in session, in milliseconds.
	 * 
	 * @param ttl
	 */
	static void setTtl(long ttl) {
		if (ttl < 0) {
			ttl = 0;
		}

		_ttl = ttl;
	}

	/**
	 * Get the time to live for the CAPTCHA, in milliseconds.
	 * 
	 * @return
	 */
	static long getTtl() {
		return _ttl;
	}

	// Expire the CAPTCHA after a given number of minutes
	static boolean shouldExpire(Captcha captcha) {
		long ts = captcha.getTimeStamp().getTime();
		long now = new Date().getTime();
		long diff = now - ts;

		return diff >= _ttl;
	}
}
