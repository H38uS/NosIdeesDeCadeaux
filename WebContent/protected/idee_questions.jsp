<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<c:choose>
			<c:when test="${isOwner}">
				<h2>Répondez à des questions sur une de vos idées</h2>
			</c:when>
			<c:otherwise>
				<h2>Posez une question sur une idée</h2>
			</c:otherwise>
		</c:choose>
		<div>Rappel de l'idée : ${text}</div>

		<c:if test="${not empty success}">
			Votre nouveau commentaire a bien été ajouté.
		</c:if>
	
		<div>
			<c:choose>
				<c:when test="${isOwner}">
					<h3>Répondez aux questions / Laissez un message pour cette idée</h3>
				</c:when>
				<c:otherwise>
					<h3>Posez une question à ${owner.name}</h3>
				</c:otherwise>
			</c:choose>
			<form action="protected/idee_questions" method="post">
				<table>
					<tr>
						<td>
							<c:choose>
								<c:when test="${isOwner}">
									<label>Votre réponse / commentaire</label>
								</c:when>
								<c:otherwise>
									<label>Votre question</label>
								</c:otherwise>
							</c:choose>
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

		<h3>Questions / réponses existantes</h3>
		<div>
			<c:choose>
				<c:when test="${empty comments}">
					Aucun commentaire sur l'idée pour le moment.
				</c:when>
				<c:otherwise>
					<c:forEach var="comment" items="${comments}" >
						<div class="comment">
							<c:choose>
								<c:when test="${userid == comment.writtenBy.id}">
									<div class="comment_header_mine">Posté par vous le ${comment.time} - le <a href="protected/supprimer_question?id=${comment.id}">supprimer</a></div>
								</c:when>
								<c:when test="${comment.writtenBy.id == owner.id}">
									<div class="comment_header_owner">Posté par ${owner.name} le ${comment.time}</div>
								</c:when>
								<c:otherwise>
									<div class="comment_header_other">Posté par quelqu'un le ${comment.time}</div>
								</c:otherwise>
							</c:choose>
							<div class="comment_text">${comment.text}</div>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</jsp:body>
</t:normal_protected>