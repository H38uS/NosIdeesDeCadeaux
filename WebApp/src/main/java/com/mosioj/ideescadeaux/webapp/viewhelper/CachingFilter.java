package com.mosioj.ideescadeaux.webapp.viewhelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Provides helper functions to the views.
 *
 * @author Jordan Mosio
 */
public class CachingFilter implements Filter {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(CachingFilter.class);

    /** Number of seconds of the caching. => currently 60 days */
    private static final long cacheAgeInSeconds = 3600 * 24 * 60;

    /** Number of milliseconds of the caching. => currently 60 days */
    private static final long cacheAgeInMillis = cacheAgeInSeconds * 1000;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        if (!(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        logger.trace("Entering CachingFilter...");
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest detailRequest = (HttpServletRequest) request;

        String url = detailRequest.getRequestURL().toString();
        logger.trace(MessageFormat.format("Adding {0} to the cache...", url));
        long expiry = new Date().getTime() + cacheAgeInMillis;
        httpResponse.setDateHeader("Expires", expiry);
        httpResponse.setHeader("Cache-Control", "max-age=" + cacheAgeInSeconds);

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
