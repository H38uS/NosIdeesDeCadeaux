package com.mosioj.ideescadeaux.webapp.filter;

import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import com.mosioj.ideescadeaux.webapp.servlets.logichelpers.CompteInteractions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHashUpdateFilter extends GenericFilterBean {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(PasswordHashUpdateFilter.class);

    /** Password hashing. */
    private static MessageDigest md;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest detailRequest = (HttpServletRequest) request;
        String url = detailRequest.getRequestURL().toString();

        if (url.endsWith("/login") && md != null) {

            final String email = request.getParameter("j_username");
            final String pwd = request.getParameter("j_password");
            UsersRepository.getUser(email)
                           .filter(u -> !StringUtils.isBlank(pwd))
                           .filter(u -> oldHash(pwd).equals(u.getPassword()))
                           .ifPresent(u -> {
                               logger.info("Migrating hash algorithm for {}...", email);
                               u.setPassword(CompteInteractions.hashPwd(pwd));
                               HibernateUtil.update(u);
                           });
        }
        chain.doFilter(request, response);
    }

    /**
     * @param rawPassword The password as text.
     * @return The password hashed, using the old way
     */
    private String oldHash(String rawPassword) {
        StringBuilder hashPwd = new StringBuilder();
        md.update(rawPassword.getBytes());
        byte[] digest = md.digest();
        for (byte b : digest) {
            hashPwd.append(String.format("%02x", b & 0xff));
        }
        return hashPwd.toString();
    }

    static {
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Fail to initialize the password hash.", e);
        }
    }
}