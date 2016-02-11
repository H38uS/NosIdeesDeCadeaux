package com.mosioj.tests.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import nl.captcha.Captcha;
import nl.captcha.Captcha.Builder;
import nl.captcha.text.producer.TextProducer;

public class CaptchaMockFactory {

	public static Captcha getValidCaptcha(HttpServletRequest mockRequest) {
		TextProducer producer = mock(TextProducer.class);
		when(producer.getText()).thenReturn("mytext");
		when(mockRequest.getParameter("answer")).thenReturn("mytext");
		return new Builder(20, 20).addText(producer).build();
	}
	
	public static Captcha getInvalidCaptcha(HttpServletRequest mockRequest) {
		TextProducer producer = mock(TextProducer.class);
		when(producer.getText()).thenReturn("mytext");
		when(mockRequest.getParameter("answer")).thenReturn("notmatchingmytext");
		return new Builder(20, 20).addText(producer).build();
	}
}
