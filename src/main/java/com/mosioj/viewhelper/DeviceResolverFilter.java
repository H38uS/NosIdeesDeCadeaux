package com.mosioj.viewhelper;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;

/**
 * Provides helper functions to the views.
 * 
 * @author Jordan Mosio
 *
 */
public class DeviceResolverFilter implements Filter {

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(DeviceResolverFilter.class);

	/**
	 * Spring device resolver.
	 */
	private static final DeviceResolver DEVICE_RESOLVER = new LiteDeviceResolver();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		logger.trace("Entering device resolver filtering...");
		logger.debug("URL: " + ((HttpServletRequest) request).getRequestURL());
		HttpSession session = ((HttpServletRequest) request).getSession();
		Object isMobile = session.getAttribute("is_mobile");
		if (isMobile == null) {
			logger.debug("Resolving the device...");
			Device device = DEVICE_RESOLVER.resolveDevice(((HttpServletRequest) request));
			session.setAttribute("device", device);
			session.setAttribute("is_mobile", device.isMobile());
			session.setAttribute("is_normal", device.isNormal());
			session.setAttribute("action_img_width", device.isMobile() ? "80" : "30");
		}
		logger.debug(MessageFormat.format("Is mobile ? {0}", session.getAttribute("is_mobile")));
		request.setAttribute("is_mobile", session.getAttribute("is_mobile"));
		request.setAttribute("is_normal", session.getAttribute("is_normal"));
		request.setAttribute("action_img_width", session.getAttribute("action_img_width"));

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Nothing to do
	}

	@Override
	public void destroy() {
		// Nothing to do
	}
}
