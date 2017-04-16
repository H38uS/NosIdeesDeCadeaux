<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_proctected>
		<jsp:body>
		<c:if test="${not empty group}">
			<h2>Partager ce groupe</h2>
			<c:if test="${not empty sent_to_users}">
				La requête a bien été envoyé aux utilisateurs suivants :
				<ul>
					<c:forEach var="user" items="${sent_to_users}">
						<li>${user.name}</li>
					</c:forEach>
				</ul>
			</c:if>
			<c:if test="${not empty candidates}">
				Partagez ce groupe avec :
				<form method="POST" action="protected/suggerer_groupe_idee">
					<table>
						<c:forEach var="user" items="${candidates}">
							<tr>
								<td>
									<input type="checkbox" id="cb${user.id}" name="${user.id}" />
								</td>
								<td><label for="cb${user.id}" >${user.name}</label></td>
							</tr>
						</c:forEach>
					</table>
					<input type="hidden" name="groupid" value="${group.id}" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="submit" name="submit" id="submit" value="Suggérer !" />
				</form>
			</c:if>
			<c:if test="${empty candidates}">
				Vous ne connaissais personne qui serait susceptible de participer à ce cadeau...
			</c:if>
			<h3>Rappel de l'idée</h3>
			<div>${idea.text}</div>
			<h3>Détail de ce groupe</h3>
			<div>
				<div>Montant total souhaité : ${group.total}</div>
				<div>
					<c:choose>
						<c:when test="${empty group.shares}">
							Aucune participation pour le moment.
						</c:when>
						<c:otherwise>
							<table>
								<caption>
									<th>Participant</th>
									<th>Montant</th>
								</caption>
								<c:forEach var="share" items="${group.shares}">
									<tr>
										<td>${share.user.name}</td>
										<td>${share.amount}</td>
									</tr>
								</c:forEach>
							</table>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="errors">
				<c:if test="${fn:length(errors) > 0}">
					<p>Des erreurs sont survenues:</p>
					<ul>
						<c:forEach var="error" items="${errors}">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</c:if>
			</div>
		</c:if>
		<c:if test="${empty group}">
			Le groupe n'existe pas ou vous ne pouvez pas intéragir avec.
		</c:if>
	</jsp:body>
</t:normal_proctected>