<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal_public>
	<jsp:body>
		<div class="login_form">
			<h2 class="fl_yellow">Création de compte</h2>
			<div>
				<form method="POST" action="creation_compte">
					<table>
						<tr>
							<td>
								<label for="email" class="required fl_green">Adresse email</label>
							</td>
							<td>
								<input type="email" name="email" id="email" value="${param.email}" required />
							</td>
						</tr>
						<c:if test="${fn:length(email_errors) > 0}">
							<c:forEach var="error" items="${email_errors}">
								<tr>
									<td colspan="2" class="error">${error}</td>
								</tr>
							</c:forEach>
						</c:if>
						<tr>
							<td>
								<label for="pwd" class="required fl_blue">Mot de passe</label>
							</td>
							<td>
								<input type="password" name="pwd" id="pwd" value="${param.pwd}" required />
							</td>
						</tr>
						<c:if test="${fn:length(pwd_errors) > 0}">
							<c:forEach var="error" items="${pwd_errors}">
								<tr>
									<td colspan="2" class="error">${error}</td>
								</tr>
							</c:forEach>
						</c:if>
						<tr>
							<td>
								<img src="stickyImg" />
							</td>
							<td>
								<input name="answer" type="text" value="${param.answer}" />
							</td>
						</tr>
						<c:if test="${fn:length(captcha_errors) > 0}">
							<tr>
								<td colspan="2" class="error">${captcha_errors}</td>
							</tr>
						</c:if>
						<tr>
							<td class="fl_purple">Pseudo</td>
							<td>
								<input name="pseudo" type="text" value="${param.pseudo}" />
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<input type="submit" name="submit" id="submit" value="Créer !" />
							</td>
						</tr>
					</table>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				</form>
			</div>
		</div>
	</jsp:body>
</t:normal_public>