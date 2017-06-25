<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_proctected>
	<jsp:body>
		<c:if test="${not empty accepted}">
			<h2>Succès</h2>
			Les demandes suivantes ont été accepté avec succès.
			<ul>
				<c:forEach var="accept" items="${accepted}">
					<li>
						${accept.name} :
						<a href="protected/suggerer_relations?id=${accept.id}">Suggérer</a> des relations
					</li>
				</c:forEach>
			</ul>
		</c:if>
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
				<input type="hidden" name="id" value="${id}" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</c:if>
		<c:if test="${not empty suggestions && suggestions}">
			Vos amis vous suggèrent de nouvelles relations ! <a href="protected/suggestion_amis">Aller voir</a>...
		</c:if>
		<h2>Réseau de ${name}</h2>
		<c:choose>
			<c:when test="${empty relations}">
				Vous n'avez aucune relation pour le moment. <a href="protected/rechercher_personne.jsp" >Rechercher</a> des personnes à ajouter !
			</c:when>
			<c:otherwise>
				<table>
					<c:set var="count" value="0" scope="page" />
					<c:forEach var="relation" items="${relations}">
						<c:if test="${count % 4 == 0}">
							<tr>
						</c:if>
							<td>
								<img src="${relation.second.avatarSrcSmall}"><br/>
								<a href="protected/afficher_reseau?id=${relation.second.id}">${relation.second.name}</a><br/>
								<c:if test="${relation.second.id != userid}">
									<a href="protected/suggerer_relations?id=${relation.second.id}">Suggérer</a> des relations.<br/>
									Lui <a href="protected/ajouter_idee_ami?id=${relation.second.id}">ajouter</a> une idée.
								</c:if>
							</td>
						<c:set var="count" value="${count + 1}" scope="page"/>
						<c:if test="${count % 4 == 0}">
							</tr>
						</c:if>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
	</jsp:body>
</t:normal_proctected>