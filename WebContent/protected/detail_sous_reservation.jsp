<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>Détail de la réservation partielle</h2>
		<div>
			<h3>Le text de l'idée</h3>
			${idea.text}
		</div>
		<div>
			<h3>Les sous réservations existantes</h3>
		Les sous réservations existantes ...
		</div>
		<div>
			<h3>Ajouter la vôtre !</h3>
			<c:if test="${not empty idea}">
				<form action="protected/sous_reserver" method="post" >
					<table>
						<input type="hidden" name="idee" value="${idea.id}">
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
			<c:if test="${empty idea}">
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
		<h3>Suggérer ce groupe à quelqu'un</h3>
		Il manque un peu... N'hésitez plus, <a href="protected/suggerer_groupe_idee?groupid=${group.id}">suggérer</a> ce groupe à d'autres personnes !
	</jsp:body>
</t:normal_protected>