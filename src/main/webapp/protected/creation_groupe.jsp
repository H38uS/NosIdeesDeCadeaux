<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal>
	<jsp:body>
		<h2>Création de groupe</h2>
		<div>
			<form method="POST" action="protected/creation_groupe">
				<table>
					<tr>
						<td>
							<label for="name" class="required">Nom du groupe</label>
						</td>
						<td>
							<input type="text" name="name" id="name" value="${param.name}" />
						</td>
					</tr>
					<c:if test="${fn:length(name_errors) > 0}">
						<c:forEach var="error" items="${name_errors}">
							<tr>
								<td colspan="2" class="error">${error}</td>
							</tr>
						</c:forEach>
					</c:if>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
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