package com.mosioj.ideescadeaux.webapp.viewhelper;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.utils.AppVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Provides helper functions to the views.
 *
 * @author Jordan Mosio
 */
public class DeviceResolverFilter implements Filter {

    private static final String PICTURE_PC_WIDTH = "36";

    /**
     * Class logger.
     */
    private static final Logger logger = LogManager.getLogger(DeviceResolverFilter.class);

    /**
     * Spring device resolver.
     */
    private static final DeviceResolver DEVICE_RESOLVER = new LiteDeviceResolver();

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        logger.trace("Entering device resolver filtering...");

        HttpServletRequest detailRequest = (HttpServletRequest) request;
        String url = detailRequest.getRequestURL().toString();
        if (url.contains("/protected/files/")) {
            chain.doFilter(request, response);
            return;
        }

        // Setting up the version
        request.setAttribute("application_version", AppVersion.DA_VERSION);

        Device device = DEVICE_RESOLVER.resolveDevice(detailRequest);
        request.setAttribute("device", device);
        request.setAttribute("is_mobile", device.isMobile());
        request.setAttribute("is_normal", device.isNormal());
        request.setAttribute("action_img_width",
                             device.isMobile() ? IdeesRepository.MOBILE_PICTURE_WIDTH : PICTURE_PC_WIDTH);

        User user = (User) ((HttpServletRequest) request).getSession().getAttribute("connected_user");
        String name = user == null ? "anonymous" : user.getEmail();
        logger.debug(MessageFormat.format("URL: {0}, demandée par {1}. Is mobile: {2}.", url, name, device.isMobile()));
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Nothing to do
    }

    @Override
    public void destroy() {
        // Nothing to do
    }
}
