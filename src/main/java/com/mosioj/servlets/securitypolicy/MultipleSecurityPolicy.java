package com.mosioj.servlets.securitypolicy;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.utils.NotLoggedInException;

/**
 * Fails if at least one fails.
 * 
 * @author Jordan Mosio
 *
 */
public class MultipleSecurityPolicy extends SecurityPolicy {

	private SecurityPolicy[] policies;

	public MultipleSecurityPolicy(SecurityPolicy... policies) {
		this.policies = policies;
	}

	@Override
	public boolean hasRightToInteractInGetRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		for (SecurityPolicy policy : policies) {
			if (!policy.hasRightToInteractInGetRequest(request, response)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isGetRequestAllowed() {
		for (SecurityPolicy policy : policies) {
			if (!policy.isGetRequestAllowed()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean hasRightToInteractInPostRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, NotLoggedInException {
		for (SecurityPolicy policy : policies) {
			if (!policy.hasRightToInteractInPostRequest(request, response)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isPostRequestAllowed() {
		for (SecurityPolicy policy : policies) {
			if (!policy.isPostRequestAllowed()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getLastReason() {
		for (SecurityPolicy policy : policies) {
			String lastReason = policy.getLastReason();
			if (lastReason != null && !lastReason.isEmpty()) {
				return lastReason;
			}
		}
		return "";
	}

}
