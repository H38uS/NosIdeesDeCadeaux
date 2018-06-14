<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_protected>
	<jsp:body>
		<table>
			<c:forEach var="user" items="${users}">
			<tr>
				<td>${user}</td>
				<td>
					<form method="POST" action="protected/connexion_enfant">
						<input type="hidden" name="name" value="${user.id}" />
						<input type="submit" value="Se connecter avec ce compte" />
					</form>
				</td>
			</tr>
			</c:forEach>
		</table>
	</jsp:body>
</t:normal_protected>