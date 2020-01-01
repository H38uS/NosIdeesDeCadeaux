<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>Réservation d'une partie de l'idée</h2>
		<c:if test="${fn:length(errors) > 0}">
			<div class="alert alert-danger">
				<p>Des erreurs ont empêché la réservation d'une partie de cette idée:</p>
				<ul>
					<c:forEach var="error" items="${errors}">
						<li>${error}</li>
					</c:forEach>
				</ul>
			</div>
		</c:if>
			<c:if test="${not empty idee}">
				<div class="container">
					<form action="protected/sous_reserver" method="post" >
						<input type="hidden" name="idee" value="${idee.id}">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="form-group">
							<label for="comment" class="d-none d-md-inline-block">Commentaire de la réservation</label>
							<textarea id="comment" class="form-control" name="comment" required="required" cols="50" rows="5" placeholder="Je prends le tome 42 de la série..." >${comment}</textarea>
						</div>
						<div class="center">
							<button class="btn btn-primary" type="submit" name="submit" id="submit">Réserver !</button>
						</div>
					</form>
				</div>
				<h2>Rappel de l'idée</h2>
				<t:template_une_idee />
			</c:if>
			<c:if test="${empty idee}">
				L'idée que vous souhaitez réserver n'existe pas, ou vous n'avez pas les droits pour le faire.
			</c:if>
	</jsp:body>
</t:normal_protected>