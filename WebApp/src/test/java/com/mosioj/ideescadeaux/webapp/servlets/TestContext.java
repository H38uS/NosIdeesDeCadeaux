package com.mosioj.ideescadeaux.webapp.servlets;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.WebAppTemplateTest;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.ChangerMotDePasseDepuisReinit;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.CreationCompte;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.MesNotifications;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AjouterIdee;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.IdeaComments;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.IdeeQuestions;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification.AjouterIdeeAmi;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.reservation.GroupIdeaDetails;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.GsonFactory;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestContext {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(TestContext.class);

    /** The only allowed instance. */
    private static TestContext instance;

    // ================ Mocked fields

    /** The mocked request */
    private HttpServletRequest request;

    /** The mocked response */
    private final HttpServletResponse response;

    /** Service response */
    private final MyServerOutput responseOutput = new MyServerOutput();

    /** Request dispatcher */
    private final RequestDispatcher dispatcher;

    /** Request session */
    private final HttpSession session;

    /** Request device */
    private final Device device;

    private TestContext() {
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        device = mock(Device.class);
        when(session.getAttributeNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(response.getCharacterEncoding()).thenReturn("UTF-8");
        try {
            when(response.getOutputStream()).thenReturn(responseOutput);
        } catch (IOException e) {
            fail();
        }
        reset();
    }

    /**
     * Only reset the request parts and the connected user back to firefox.
     */
    public void reset() {
        request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestURL()).thenReturn(new StringBuffer(CreationCompte.HTTP_LOCALHOST_8080));
        when(request.getContextPath()).thenReturn("");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(request.getAttribute("device")).thenReturn(device);
        setConnectedUserTo(WebAppTemplateTest.firefox);

        when(request.getRequestDispatcher(IdeeQuestions.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getRequestDispatcher(AjouterIdeeAmi.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getRequestDispatcher(MesNotifications.URL)).thenReturn(dispatcher);
        when(request.getRequestDispatcher(GroupIdeaDetails.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getRequestDispatcher(RootingsUtils.PUBLIC_SERVER_ERROR_JSP)).thenReturn(dispatcher);
        when(request.getRequestDispatcher(CreationCompte.FORM_URL)).thenReturn(dispatcher);
        when(request.getRequestDispatcher(CreationCompte.SUCCES_URL)).thenReturn(dispatcher);
        when(request.getRequestDispatcher(ChangerMotDePasseDepuisReinit.SUCCES_PAGE_URL)).thenReturn(dispatcher);
        when(request.getRequestDispatcher(AjouterIdee.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getRequestDispatcher(IdeaComments.VIEW_PAGE_URL)).thenReturn(dispatcher);

        when(request.getRequestDispatcher("/protected/erreur_parametre_ou_droit.jsp")).thenReturn(dispatcher);
    }

    /**
     * @return The emulated device.
     */
    public Device getDevice() {
        return device;
    }

    /**
     * @return The only allowed instance.
     */
    public static TestContext getInstance() {
        if (instance == null) {
            instance = new TestContext();
        }
        return instance;
    }

    // ================ Utility methods to control the parameters and requests

    /**
     * Set up a request parameter
     *
     * @param parameterName The parameter to mock
     * @param value         The value to return
     */
    public void bindGetRequestParam(String parameterName, Object value) {
        when(request.getParameter(parameterName)).thenReturn(value.toString());
    }

    /**
     * Set up a request parameter
     *
     * @param parameterName The parameter to mock
     * @param value         The value to return
     */
    public void bindPostRequestParam(String parameterName, Object value) {
        // Emulate the fact that tomcat is using ISO_8859_1 as the default encoding...
        final String convertedValue = new String(value.toString().getBytes(StandardCharsets.UTF_8),
                                                 StandardCharsets.ISO_8859_1);
        when(request.getParameter(parameterName)).thenReturn(convertedValue);
    }

    /**
     * Set up request parameters as a map
     *
     * @param parameters The parameters map
     */
    public void bindRequestParamMap(Map<String, String[]> parameters) {
        when(request.getParameterMap()).thenReturn(parameters);
    }

    /**
     * @param user The new connected user
     */
    public void setConnectedUserTo(User user) {
        when(session.getAttribute("connected_user")).thenReturn(user);
    }

    // ================ POST / GET methods

    /**
     * Performs a post to the test object.
     */
    public void doTestPost(IdeesCadeauxServlet<? extends SecurityPolicy> service) {
        when(request.getMethod()).thenReturn("POST");
        service.doPost(request, response);
    }

    /**
     * Performs a post to the test object.
     *
     * @param clazz The actual class of the response.
     * @return The service response.
     */
    public <T> T doTestServicePost(IdeesCadeauxServlet<? extends SecurityPolicy> service, Class<T> clazz) {
        when(request.getMethod()).thenReturn("POST");
        responseOutput.clear();
        service.doPost(request, response);
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
    public <T> T doTestServiceGet(IdeesCadeauxServlet<? extends SecurityPolicy> service, Class<T> clazz) {
        when(request.getMethod()).thenReturn("GET");
        responseOutput.clear();
        service.doGet(request, response);
        logger.info("Service response: " + responseOutput.builder);
        T resp = GsonFactory.getIt().fromJson(responseOutput.builder.toString(), clazz);
        assertNotNull(resp);
        return resp;
    }

    /**
     * Performs a get to the test object.
     */
    public void doTestGet(IdeesCadeauxServlet<? extends SecurityPolicy> service) {
        when(request.getMethod()).thenReturn("GET");
        service.doGet(request, response);
    }

    public void createMultiPartRequest(Map<String, String> parameters) throws IOException {

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

    // Utility classes

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
