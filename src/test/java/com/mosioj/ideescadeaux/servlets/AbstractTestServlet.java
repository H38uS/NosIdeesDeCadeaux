package com.mosioj.ideescadeaux.servlets;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.utils.GsonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;

import com.mosioj.ideescadeaux.servlets.controllers.compte.CreationCompte;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.TemplateTest;

public abstract class AbstractTestServlet extends TemplateTest {

    protected RequestDispatcher dispatcher;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;
    protected Device device;

    private static final Logger logger = LogManager.getLogger(AbstractTestServlet.class);
    protected final IdeesCadeauxGetAndPostServlet<? extends SecurityPolicy> instance;
    private final MyServerOutput responseOutput = new MyServerOutput();

    public AbstractTestServlet(IdeesCadeauxGetAndPostServlet<? extends SecurityPolicy> pInstance) {

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
        instance.setIdeaPicturePath(new File("C:\\temp"));

        try {
            validateInstanceLinks();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            fail();
        }
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
    protected void doTestPost(HttpServletRequest request, HttpServletResponse response) {
        when(request.getMethod()).thenReturn("POST");
        try {
            instance.doPost(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            fail("Servlet error.");
        }
    }

    /**
     * Performs a post to the test object.
     */
    protected ServiceResponse doTestServicePost() {
        when(request.getMethod()).thenReturn("POST");
        responseOutput.clear();
        try {
            instance.doPost(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            fail("Servlet error.");
        }
        logger.info(responseOutput.builder);
        ServiceResponse resp = GsonFactory.getIt().fromJson(responseOutput.builder.toString(), ServiceResponse.class);
        assertNotNull(resp);
        return resp;
    }

    /**
     * Performs a post to the test object.
     *
     * @param clazz The actual class of the response.
     */
    protected <T> T doTestServiceGet(Class<T> clazz) {
        when(request.getMethod()).thenReturn("GET");
        responseOutput.clear();
        try {
            instance.doGet(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            fail("Servlet error.");
        }
        logger.info("Service response: " + responseOutput.builder);
        T resp = GsonFactory.getIt().fromJson(responseOutput.builder.toString(), clazz);
        assertNotNull(resp);
        return resp;
    }

    /**
     * Performs a get to the test object.
     */
    protected void doTestGet(HttpServletRequest request, HttpServletResponse response) {
        when(request.getMethod()).thenReturn("GET");
        try {
            instance.doGet(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            fail("Servlet error.");
        }
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
