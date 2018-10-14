<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
	<script src="resources/js/admin.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
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
				<td>
					<form class="form_suppression_compte" method="POST" action="administration/suppression_compte">
						<input type="hidden" name="name" value="${user.id}" />
						<input class="form_suppression_compte_submit" type="submit" value="Supprimer ce compte" />
					</form>
				</td>
			</tr>
			</c:forEach>
		</table>
	</jsp:body>
</t:template_body_protected>