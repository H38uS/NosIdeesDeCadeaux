<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_protected>
	<jsp:body>
		<c:if test="${not empty accepted}">
			<h2>Succès</h2>
			Les demandes suivantes ont été acceptées avec succès.
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
			<div class="login_form">
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
									<input type="radio" id="acc_choix_${demande.sent_by.id}" name="choix_${demande.sent_by.id}" value="Accepter">
									<label for="acc_choix_${demande.sent_by.id}">Accepter</label>
								</td>
								<td>
									<input type="radio" id="ref_choix_${demande.sent_by.id}" name="choix_${demande.sent_by.id}" value="Refuser">
									<label for="ref_choix_${demande.sent_by.id}">Refuser</label>
								</td>
							</tr>
						</c:forEach>
					</table>
					<input type="submit" id="submit" name="submit" value="Sauvegarder">
					<input type="hidden" name="id" value="${id}" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				</form>
			</div>
		</c:if>
		<c:if test="${not empty suggestions && suggestions}">
			Vos amis vous suggèrent de nouvelles relations ! <a href="protected/suggestion_amis">Aller voir</a>...
		</c:if>
		<h2>Rechercher des personnes dans le réseau de ${name}</h2>
			<form method="POST" action="protected/rechercher_reseau">
				<table>
					<tr>
						<td>
							<label for="name" class="required">Nom / Email de la personne</label>
						</td>
						<td>
							<input type="text" name="looking_for" id="looking_for" value="${looking_for}" />
						</td>
						<td></td>
						<td align="center">
							<input type="submit" name="submit" id="submit" value="Rechercher !" />
						</td>
					</tr>
				</table>
				<input type="hidden" name="id" value="${id}" />
				<input type="hidden" name="page" value="1" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		<h2>Réseau de ${name}</h2>
		<c:if test="${not empty pages}">
			<div class="center">
				<c:if test="${current != 1}">
					<a href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
				</c:if>
				<c:forEach var="page" items="${pages}">
					<c:choose>
						<c:when test="${current != page.numero}">
							<a href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
						</c:when>
						<c:otherwise>
							${page.numero}
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<c:if test="${current != last}">
					<a href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
				</c:if>
			</div>
		</c:if>
		<c:choose>
			<c:when test="${empty entities}">
				Aucune relation trouvée. <a href="protected/rechercher_personne.jsp" >Rechercher</a> des personnes à ajouter !
			</c:when>
			<c:otherwise>
				<ul id="person_square_container">
					<c:forEach var="relation" items="${entities}">
						<li class="person_square">
							<div class="vertical_center_div">
								<span class="verticalcenter_helper"></span>
								<img class="verticalcenter" src="${avatars}/${relation.second.avatarSrcSmall}">
							</div>
							<a href="protected/afficher_reseau?id=${relation.second.id}">${relation.second.name}</a><br/>
							<div class="person_square_action">
								<c:choose>
									<c:when test="${relation.second.id != userid && relation.secondIsInMyNetwork}">
										<a href="protected/suggerer_relations?id=${relation.second.id}">Suggérer</a> des relations.<br/>
										Lui <a href="protected/ajouter_idee_ami?id=${relation.second.id}">ajouter</a> une idée.<br/>
										<a href="protected/supprimer_relation?id=${relation.second.id}">Supprimer</a> cette personne.
									</c:when>
									<c:when test="${relation.second.id == userid}">
										Vous ne pouvez pas intéragir avec vous-même !
									</c:when>
									<c:otherwise>
										Vous n'êtes pas encore ami avec cette personne.<br/>
										<c:choose>
											<c:when test="${not empty relation.second.freeComment}">
												${relation.second.freeComment}
											</c:when>
											<c:otherwise>
												<form method="POST" action="protected/demande_rejoindre_reseau">
													<input type="hidden" name="user_id" value="${relation.second.id}" >
													<input type="submit" name="submit" id="submit" value="Envoyer une demande" />
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												</form>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</div>
						</li>
					</c:forEach>
				</ul>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty pages}">
			<div class="center">
				<c:if test="${current != 1}">
					<a href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
				</c:if>
				<c:forEach var="page" items="${pages}">
					<c:choose>
						<c:when test="${current != page.numero}">
							<a href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
						</c:when>
						<c:otherwise>
							${page.numero}
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<c:if test="${current != last}">
					<a href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
				</c:if>
			</div>
		</c:if>
	</jsp:body>
</t:normal_protected>