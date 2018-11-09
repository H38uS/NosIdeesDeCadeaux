<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
	<jsp:body>
		<p>
		A la différence des questions, cette page permet de discuter entre participant autour d'une idée, pour mieux s'organiser.
		Le propriétaire de l'idée ne pourra donc rien voir de ce qu'il se passe ici !
		</p>
		<h2>Commenter une idée</h2>
		<h3>Rappel de l'idée</h3>
		<t:template_une_idee />

		<c:if test="${not empty success}">
			Votre nouveau commentaire a bien été ajouté.
		</c:if>
	
		<div>
			<h3>Ajouter un nouveau commentaire</h3>
			<div class="container">
				<form action="protected/idee_commentaires" method="post">
					<div class="form-group">
						<label class="d-none d-md-inline-block">Votre message</label>
						<textarea id="text" class="form-control" name="text" cols="70" rows="6" required="required" placeholder="Votre commentaire..."></textarea>
					</div>
					<div class="center">
						<button class="btn btn-primary" type="submit" name="submit">Ajouter !</button>
					</div>
					<input type="hidden" name="idee" value="${idee.id}" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				</form>
			</div>
		</div>

		<h3 class="mt-2">Commentaires existants</h3>
		<div class="container">
			<c:choose>
				<c:when test="${empty comments}">
					Aucun commentaire sur l'idée pour le moment.
				</c:when>
				<c:otherwise>
					<c:forEach var="comment" items="${comments}" >
						<c:choose>
							<c:when test="${userid == comment.writtenBy.id}">
								<div class="comment comment_mine">
									<div class="comment_header_mine">Posté par vous le ${comment.time} - le <a href="protected/supprimer_commentaire?id=${comment.id}">supprimer</a></div>
									<div class="comment_text">${comment.text}</div>
								</div>
							</c:when>
							<c:otherwise>
								<div class="comment comment_other">
									<div class="comment_header_other">Posté par ${comment.writtenBy.name} le ${comment.time}</div>
									<div class="comment_text">${comment.text}</div>
								</div>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</jsp:body>
</t:normal_protected>