package com.mosioj.ideescadeaux.utils;

import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import org.junit.Test;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class TestParametersUtils {

    @Test
    public void testValidTest() {
        assertEquals(Optional.of(32d),
                     ParametersUtils.readDouble(new TestHttpServletRequest(32), "toto"));
        assertEquals(Optional.of(32.15), ParametersUtils.readDouble(new TestHttpServletRequest("32.15"), "toto"));
        assertEquals(Optional.of(32.15),
                     ParametersUtils.readDouble(new TestHttpServletRequest(MessageFormat.format("{0}.15", 32)),
                                                "toto"));

        TestAllGetAndPost p = new TestAllGetAndPost();

        assertEquals(new Integer(32), p.readIntTest(new TestHttpServletRequest("32"), "toto").orElse(0));
        assertEquals(new Integer(32),
                     p.readIntTest(new TestHttpServletRequest(MessageFormat.format("{0}", 32)), "toto").orElse(0));
        assertEquals(new Integer(1032), p.readIntTest(new TestHttpServletRequest("1032"), "toto").orElse(0));
    }

    private static class TestAllGetAndPost extends SecurityPolicy {
        public Optional<Integer> readIntTest(HttpServletRequest request, String name) {
            return readInt(request, name);
        }

        @Override
        public boolean hasRightToInteractInGetRequest(HttpServletRequest request,
                                                      HttpServletResponse response) {
            return true;
        }

        @Override
        public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) {
            return true;
        }

        @Override
        public void reset() {
        }
    }

    private static class TestHttpServletRequest implements HttpServletRequest {

        private String param;

        public TestHttpServletRequest(Object value) {
            param = value.toString();
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String env) {

        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return param;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return null;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public BufferedReader getReader() {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object o) {

        }

        @Override
        public void removeAttribute(String name) {

        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public String getRealPath(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
        }

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(String name) {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public int getIntHeader(String name) {
            return 0;
        }

        @Override
        public String getMethod() {
            return null;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public String getServletPath() {
            return null;
        }

        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }

        @Override
        public boolean authenticate(HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(String username, String password) {
        }

        @Override
        public void logout() {
        }

        @Override
        public Collection<Part> getParts() {
            return null;
        }

        @Override
        public Part getPart(String name) {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
            return null;
        }

    }
}
