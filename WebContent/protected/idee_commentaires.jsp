<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_proctected>
		<jsp:body>
		<h2>Commenter une idée</h2>
		<div>Rappel de l'idée : ${text}</div>

		<c:if test="${not empty success}">
			Votre nouveau commentaire a bien été ajouté.
		</c:if>
	
		<h3>Ajouter un nouveau commentaire</h3>
		<form action="protected/idee_commentaires" method="post">
			<div>
				<textarea id="text" name="text" cols="70" rows="6">${idea.text}</textarea>
			</div>
			<input type="hidden" name="idee" value="${idee}" />
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="submit" name="submit" value="Ajouter !">
		</form>

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
								<div>Posté par vous</div>
							</c:when>
							<c:otherwise>
								<div>Posté par ${comment.writtenBy.name}</div>
							</c:otherwise>
						</c:choose>
						<div>${comment.text}</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</jsp:body>
</t:normal_proctected>