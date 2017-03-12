<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal_proctected>
	<jsp:body>
		<h2>Rechercher une personne pour l'ajouter</h2>
		<div>
			<form method="POST" action="protected/rechercher_personne">
				<table>
					<tr>
						<td>
							<label for="name" class="required">Nom / Email de la personne</label>
						</td>
						<td>
							<input type="text" name="name" id="name" value="${name}" />
						</td>
					</tr>
					<tr>
						<td>
							<label for="only_non-friend">Afficher uniquement les non-amis</label>
						</td>
						<td>
							<c:if test="${onlyNonFriend}">
								<input type="checkbox" name="only_non-friend" id="only_non-friend" checked="checked" />
							</c:if>
							<c:if test="${not onlyNonFriend}">
								<input type="checkbox" name="only_non-friend" id="only_non-friend" />
							</c:if>
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
		<div>
			<c:if test="${not empty users}">
				<table>
					<thead>
						<tr>
							<th>Nom de la personne</th>
							<th>Email</th>
							<th>Action</th>
						</tr>
					</thead>
				<c:forEach var="user" items="${users}">
					<tr>
						<td>${user.name}</td>
						<td>${user.email}</td>
						<td>
							<c:choose>
								<c:when test="${user.isInMyNetwork}">
									${user.name} fait déjà parti de vos amis.
								</c:when>
								<c:otherwise>
									<form method="POST" action="protected/demande_rejoindre_reseau">
										<input hidden="true" type="text" name="user_id" value="${user.id}" >
										<input type="submit" name="submit" id="submit" value="Envoyer une demande" />
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									</form>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
				</table>
			</c:if>
		</div>
	</jsp:body>
</t:normal_proctected>