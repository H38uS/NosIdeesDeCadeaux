package com.mosioj.servlets.securitypolicy.root;

public abstract class SecurityPolicyGetAndPost extends SecurityPolicy {

	@Override
	public final boolean isGetRequestAllowed() {
		return true;
	}

	@Override
	public final boolean isPostRequestAllowed() {
		return true;
	}

}
