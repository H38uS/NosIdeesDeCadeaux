package com.mosioj.viewhelper;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EmptyFilter implements FilterChain {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
	}

}
