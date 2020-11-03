package com.mosioj.ideescadeaux.webapp.servlets;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.WebAppTemplateTest;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.CreationCompte;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.GsonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractTestServletWebApp extends WebAppTemplateTest {

    protected RequestDispatcher dispatcher;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;
    protected Device device;

    private static final Logger logger = LogManager.getLogger(AbstractTestServletWebApp.class);
    protected final IdeesCadeauxServlet<? extends SecurityPolicy> instance;
    private final MyServerOutput responseOutput = new MyServerOutput();
    protected ServletContext config;

    public AbstractTestServletWebApp(IdeesCadeauxServlet<? extends SecurityPolicy> pInstance) {

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        device = mock(Device.class);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestURL()).thenReturn(new StringBuffer(CreationCompte.HTTP_LOCALHOST_8080));
        when(request.getContextPath()).thenReturn("");
        when(session.getAttribute("connected_user")).thenReturn(firefox);
        when(session.getAttributeNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(request.getAttribute("device")).thenReturn(device);
        when(request.getRequestDispatcher("/protected/erreur_parametre_ou_droit.jsp")).thenReturn(dispatcher);
        when(response.getCharacterEncoding()).thenReturn("UTF-8");
        try {
            when(response.getOutputStream()).thenReturn(responseOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }

        instance = pInstance;
        ServletConfig servletConfig = mock(ServletConfig.class);
        config = mock(ServletContext.class);
        when(servletConfig.getServletContext()).thenReturn(config);
        try {
            instance.init(servletConfig);
        } catch (ServletException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        try {
            validateInstanceLinks();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void registerParameter(String parameterName, Object value) {
        when(request.getParameter(parameterName)).thenReturn(value.toString());
    }

    /**
     * Tests that all links for the current tested instance exists.
     */
    private void validateInstanceLinks() throws IllegalArgumentException, IllegalAccessException {

        Field[] fields = instance.getClass().getFields();
        for (Field field : fields) {

            String name = field.getName();
            if (!name.contains("URL")) {
                continue;
            }

            String path = (String) field.get(null);
            File web = new File(root, "WebContent");

            assertTrue(web.exists());
            File file = new File(web, path);
            assertTrue("La jsp " + file + " n'existe pas.", file.exists());
        }
    }

    /**
     * Performs a post to the test object.
     */
    protected void doTestPost() {
        when(request.getMethod()).thenReturn("POST");
        instance.doPost(request, response);
    }

    /**
     * Performs a post to the test object.
     *
     * @return The service response.
     */
    public StringServiceResponse doTestServicePost() {
        return doTestServicePost(StringServiceResponse.class);
    }

    /**
     * Performs a post to the test object.
     *
     * @param clazz The actual class of the response.
     * @return The service response.
     */
    protected <T> T doTestServicePost(Class<T> clazz) {
        when(request.getMethod()).thenReturn("POST");
        responseOutput.clear();
        instance.doPost(request, response);
        logger.info(responseOutput.builder);
        T resp = GsonFactory.getIt().fromJson(responseOutput.builder.toString(), clazz);
        assertNotNull(resp);
        return resp;
    }

    /**
     * Performs a get to the test object.
     *
     * @param clazz The actual class of the response.
     * @return The service response.
     */
    protected <T> T doTestServiceGet(Class<T> clazz) {
        when(request.getMethod()).thenReturn("GET");
        responseOutput.clear();
        instance.doGet(request, response);
        logger.info("Service response: " + responseOutput.builder);
        T resp = GsonFactory.getIt().fromJson(responseOutput.builder.toString(), clazz);
        assertNotNull(resp);
        return resp;
    }

    /**
     * Performs a get to the test object.
     */
    protected void doTestGet() {
        when(request.getMethod()).thenReturn("GET");
        instance.doGet(request, response);
    }

    protected void createMultiPartRequest(Map<String, String> parameters) throws IOException {

        StringBuilder sb = new StringBuilder();
        String token = "------WebKitFormBoundaryuTTrxbWL6cN4Eumf";
        String CR_LF = "\r\n";

        sb.append("--");
        sb.append(token);
        for (String param : parameters.keySet()) {

            sb.append(CR_LF);

            sb.append("Content-Disposition: form-data; name=\"");
            sb.append(param);
            sb.append("\"");
            sb.append(CR_LF);
            sb.append(CR_LF);

            sb.append(parameters.get(param));
            sb.append(CR_LF);
            sb.append("--");
            sb.append(token);
        }
        final String content = sb.toString();
        final byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);

        ServletInputStream sis = new ServletInputStream() {
            int pos = -1;

            @Override
            public int read() {
                pos++;
                if (pos >= byteContent.length) {
                    return -1;
                }
                return byteContent[pos];
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public boolean isFinished() {
                return true;
            }
        };

        when(request.getContentType()).thenReturn("multipart/form-data; boundary=" + token);
        when(request.getInputStream()).thenReturn(sis);
        when(request.getContentLength()).thenReturn(byteContent.length);
    }

    protected static class StringServiceResponse extends ServiceResponse<String> {
        /**
         * Class constructor.
         *
         * @param isOK    True if there is no error.
         * @param message The JSon response message.
         * @param isAdmin Whether the user is an admin.
         */
        public StringServiceResponse(boolean isOK, String message, boolean isAdmin, User connectedUser) {
            super(isOK, message, isAdmin, connectedUser);
        }
    }

    private static class MyServerOutput extends ServletOutputStream {

        StringBuilder builder = new StringBuilder();

        public void clear() {
            builder = new StringBuilder();
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }

        @Override
        public void write(int b) {
            builder.append((char) b);
        }

    }
}
