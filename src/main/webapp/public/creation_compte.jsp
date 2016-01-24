<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal>
	<jsp:body>
		<h2>Création de compte</h2>
		<div>
			<form method="POST" action="creation_compte">
				<table>
					<tr>						
						<td>
							<label for="user" class="required">Identifiant</label>
						</td>
						<td>
							<input type="text" name="user" id="user" value="${param.user}" />
						</td>
					</tr>
					<c:if test="${fn:length(user_errors) > 0}">
						<c:forEach var="error" items="${user_errors}">
							<tr>
								<td colspan="2" class="error">${error}</td>
							</tr>
						</c:forEach>
					</c:if>
					<tr>
						<td>
							<label for="pwd" class="required">Mot de passe</label>
						</td>
						<td>
							<input type="password" name="pwd" id="pwd" value="${param.pwd}" />
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
							<label for="email" class="required">Adresse email</label>
						</td>
						<td>
							<input type="email" name="email" id="email" value="${param.email}" />
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
						<td colspan="2" align="center">
							<input type="submit" name="submit" id="submit" value="Créer !" />
						</td>
					</tr>
				</table>
			</form>
		</div>
	</jsp:body>
</t:normal>