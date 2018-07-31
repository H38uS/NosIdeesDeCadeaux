<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>Détail de la réservation partielle - <a href="protected/voir_liste?id=${idee.owner.id}">Liste ${idee.owner.myDName}</a></h2>
		<div>
			<h3>Le text de l'idée</h3>
			<t:template_une_idee />
		</div>
		<div>
			<h3>Les sous réservations existantes</h3>
			<table>
			<c:forEach items="${sous_reservation_existantes}" var="resa" >
				<tr>
					<td>
						<c:choose>
							<c:when test="${resa.user.id == userid}">
								<strong>Vous</strong>
							</c:when>
							<c:otherwise>
								<strong>${resa.user.name}</strong>
							</c:otherwise>
						</c:choose>
					</td>
					<td>${resa.comment}</td>
					<td>
						<c:if test="${resa.user.id == userid}">
							<form action="protected/annuler_sous_reservation" method="post" >
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<input type="hidden" name="idee" value="${idee.id}">
								<input type="submit" name="submit" id="submit" value="Annuler !" />
							</form>
						</c:if>
					</td>
				</tr>
			</c:forEach>
			</table>
		</div>
		<div>
			<h3>Ajouter la vôtre !</h3>
			<c:choose>
				<c:when test="${fait_parti_sous_reservation}">
					Vous avez déjà reservé une partie de cette idée.
				</c:when>
				<c:otherwise>
					<c:if test="${not empty idee}">
						<form action="protected/sous_reserver" method="post" >
							<table>
								<input type="hidden" name="idee" value="${idee.id}">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<tr>
									<td><label for="comment">Commentaire de la réservation</label></td>
									<td>
										<textarea id="comment" name="comment" required="required" cols="50" rows="5" placeholder="Je prends le tome 42 de la série..." >${comment}</textarea>
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<input type="submit" name="submit" id="submit" value="Réserver !" />
									</td>
								</tr>
							</table>
						</form>
					</c:if>
				</c:otherwise>
			</c:choose>
			<c:if test="${empty idee}">
				L'idée que vous souhaitez réserver n'existe pas, ou vous n'avez pas les droits pour le faire.
			</c:if>
		</div>
		<div class="errors">
			<c:if test="${fn:length(errors) > 0}">
				<p>Des erreurs ont empêché la réservation d'une partie de cette idée:</p>
				<ul>
					<c:forEach var="error" items="${errors}">
						<li>${error}</li>
					</c:forEach>
				</ul>
			</c:if>
		</div>
	</jsp:body>
</t:normal_protected>