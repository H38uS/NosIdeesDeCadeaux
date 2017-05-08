<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal_proctected>
	<jsp:body>
		<h2>Succès !</h2>
		<div>
			Les demandes ont bien été envoyées à :
			<ul>
				<c:forEach var="sentTo" items="${users}" >
					<li>${sentTo.name} <c:if test="${sentTo.name != sentTo.email}"></c:if>(${sentTo.email})</li>
				</c:forEach>
			</ul>
		</div>
		<div>Envoyer de nouvelles demandes : </div>
		<div>
			<form method="POST" action="protected/suggerer_relations">
				<table>
					<tr>
						<td>
							<label for="name" class="required">Nom / Email de la personne</label>
						</td>
						<td>
							<input type="text" name="name" id="name" />
						</td>
					</tr>
					<tr>
						<td>
							<input type="hidden" name="id" value="${user.id}" >
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="submit" name="submit" id="submit" value="Rechercher !" />
						</td>
					</tr>
				</table>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</div>
	</jsp:body>
</t:normal_proctected>