<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_proctected>
	<jsp:body>
		<c:if test="${not empty demandes}">
			<h2>Demandes reçues</h2>
			<form method="POST" action="protected/afficher_reseau">
				<table>
					<thead>
						<tr>
							<th>Nom du demandeur</th>
						</tr>
					</thead>
					<c:forEach var="demande" items="${demandes}">
						<tr>
							<td>${demande.sent_by.name}</td>
							<td>
								<label for="acc_choix_${demande.sent_by.id}">Accepter</label>
								<input type="radio" id="acc_choix_${demande.sent_by.id}" name="choix_${demande.sent_by.id}" value="Accepter">
							</td>
							<td>
								<label for="ref_choix_${demande.sent_by}">Refuser</label>
								<input type="radio" id="ref_choix_${demande.sent_by.id}" name="choix_${demande.sent_by.id}" value="Refuser">
							</td>
						</tr>
					</c:forEach>
				</table>
				<input type="submit" id="submit" name="submit" value="Sauvegarder">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</c:if>
		<h2>Réseau de ${name}</h2>
		<c:choose>
			<c:when test="${empty relations}">
				Vous n'avez aucune relation pour le moment. <a href="./protected/rechercher_personne.jsp" >Rechercher</a> des personnes à ajouter !
			</c:when>
			<c:otherwise>
				<table>
					<thead>
						<tr>
							<th>Nom de la personne</th>
						</tr>
					</thead>
					<c:forEach var="relation" items="${relations}">
						<tr>
							<td>
								<a href="protected/afficher_reseau?id=${relation.second.id}">${relation.second.name}</a></td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
	</jsp:body>
</t:normal_proctected>