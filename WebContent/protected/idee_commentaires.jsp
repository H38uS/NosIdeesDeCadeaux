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
		<div>Rappel de l'idée
			<p>${text}</p>
		</div>

		<c:if test="${not empty success}">
			Votre nouveau commentaire a bien été ajouté.
		</c:if>
	
		<div>
			<h3>Ajouter un nouveau commentaire</h3>
			<form action="protected/idee_commentaires" method="post">
				<table>
					<tr>
						<td>
							<label>Votre message</label>
						</td>
						<td>
							<textarea id="text" name="text" cols="70" rows="6" required="required">${idea.text}</textarea>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" name="submit" value="Ajouter !">
						</td>
					</tr>
				</table>
				<input type="hidden" name="idee" value="${idee}" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</div>

		<h3>Commentaires existants</h3>
		<div>
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