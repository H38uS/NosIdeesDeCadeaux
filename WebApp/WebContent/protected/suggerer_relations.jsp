<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal_protected>
	<jsp:body>
		<h3>Rechercher des personnes à suggérer à ${user.name}</h3>
		<div>
			<form method="POST" class="form-inline" action="protected/suggerer_relations">
				<div class="row align-items-center mx-0">
					<div class="col-auto d-none d-md-inline-block">
						<label for="name">Nom / Email de la personne</label>
					</div>
					<div class="col-7 col-md-auto mx-0">
						<input class="form-control" type="text" name="name" id="name" value="${name}" />
					</div>
					<div class="col-5 col-md-auto">
						<button class="btn btn-primary" type="submit" name="submit" id="submit">Rechercher !</button>
					</div>
				</div>
				<div></div>
				<input type="hidden" name="id" value="${user.id}" >
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</div>
		<c:if test="${not empty users}">
			<div class="container border border-info bg-light rounded my-3 p-2">
				<form method="POST" class="form-inline" action="protected/suggestion_rejoindre_reseau">
					<c:forEach var="suggested_user" items="${users}">
						<div class="col-12 mx-2">
							<c:choose>
								<c:when test="${empty suggested_user.freeComment}">
									<input type="checkbox" class="form-check-input" name="selected_${suggested_user.id}" id="selected_${suggested_user.id}" />
									<label class="d-inline-block" for="selected_${suggested_user.id}">${suggested_user.name} - </label>
									<label class="d-inline-block" for="selected_${suggested_user.id}">${suggested_user.email}</label>
								</c:when>
								<c:otherwise>
									${suggested_user.freeComment}
								</c:otherwise>
							</c:choose>
						</div>
					</c:forEach>
					<div class="center w-100">
						<button class="btn btn-primary" type="submit" name="submit" id="submit">Envoyer les suggestions</button>
					</div>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="hidden" name="userId" value="${user.id}" />
				</form>
			</div>
		</c:if>
		<c:if test="${empty users and not empty name}">
			<div class="p-2">
				Aucune personne trouvée.
			</div>
		</c:if>
	</jsp:body>
</t:normal_protected>