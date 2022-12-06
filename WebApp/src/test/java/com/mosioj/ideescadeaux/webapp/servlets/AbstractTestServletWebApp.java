package com.mosioj.ideescadeaux.webapp.servlets;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.WebAppTemplateTest;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import org.junit.After;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractTestServletWebApp extends WebAppTemplateTest {
    protected final IdeesCadeauxServlet<? extends SecurityPolicy> instance;
    protected ServletContext config;

    public AbstractTestServletWebApp(IdeesCadeauxServlet<? extends SecurityPolicy> pInstance) {

        instance = pInstance;
        ServletConfig servletConfig = mock(ServletConfig.class);
        config = mock(ServletContext.class);
        when(servletConfig.getServletContext()).thenReturn(config);

        try {
            instance.init(servletConfig);
            validateInstanceLinks();
        } catch (IllegalArgumentException | IllegalAccessException | ServletException e) {
            fail();
        }
    }

    @After
    public void resetMock() {
        TestContext.getInstance().reset();
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

    // Shortcuts

    protected void createMultiPartRequest(Map<String, String> parameters) throws IOException {
        TestContext.getInstance().createMultiPartRequest(parameters);
    }

    protected void doTestPost() {
        TestContext.getInstance().doTestPost(instance);
    }

    public StringServiceResponse doTestServicePost() {
        return TestContext.getInstance().doTestServicePost(instance, StringServiceResponse.class);
    }

    public <T> T doTestServicePost(Class<T> clazz) {
        return TestContext.getInstance().doTestServicePost(instance, clazz);
    }

    public void doTestGet() {
        TestContext.getInstance().doTestGet(instance);
    }

    protected <T> T doTestServiceGet(Class<T> clazz) {
        return TestContext.getInstance().doTestServiceGet(instance, clazz);
    }

    public void setConnectedUserTo(User user) {
        TestContext.getInstance().setConnectedUserTo(user);
    }

    public void bindRequestParam(String parameterName, Object value) {
        TestContext.getInstance().bindRequestParam(parameterName, value);
    }

    public void bindRequestParamMap(Map<String, String[]> parameters) {
        TestContext.getInstance().bindRequestParamMap(parameters);
    }
}
