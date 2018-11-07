<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
	<link rel="stylesheet" type="text/css" href="resources/css/lib/thickbox.css" />
	<script src="resources/js/lib/thickbox.js" type="text/javascript"></script>
	<c:choose>
		<c:when test="${is_mobile}">
		</c:when>
		<c:otherwise>
			<script src="resources/js/browser/pictures.js" type="text/javascript"></script>
		</c:otherwise>
	</c:choose>
</t:template_head_includes>
<t:template_body_protected>
	<jsp:body>
		<div class="mb-3">
			Avant d'ajouter une idée, je voudrai consulter <a href="protected/voir_liste?id=${userid}">ma liste</a>.
		</div>
		<div class="container">
			<form class="mw-50" action="protected/ma_liste?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
				<div class="form-group">
					<label for="text" class="d-none d-md-inline-block">Le texte de l'idée</label>
					<textarea id="text" class="form-control" name="text" cols="70" rows="6"></textarea>
				</div>
				<div class="form-group">
					<label for="type" class="d-none d-md-inline-block">Type</label>
					<select id="type" class="form-control" name="type">
						<option value="">Sélectionnez un type</option>
						<c:forEach var="type" items="${types}">
							<option value="${type.name}">${type.alt}</option>
						</c:forEach>
						<option value="">Autre</option>
					</select>
				</div>
				<div class="form-group">
					<label for="priority" class="d-none d-md-inline-block">Priorité</label>
					<select id="priority" class="form-control" name="priority">
						<option value="1">Sélectionnez une priorité</option>
						<c:forEach var="priorite" items="${priorites}">
							<option value="${priorite.id}">${priorite.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<input id="imageFile" name="image" type="file" accept="image/jpg, image/jpeg, image/png" />
					<div>
						<label for="imageFile" class="btn btn-secondary">Ajouter une image</label>
					</div>
					<span class="d-none d-md-inline-block">Fichier Choisi: </span>
					<span id="newImage" class="picture_not_drag input"></span>
					<div>
						<img id="imageFilePreview" alt="" src="" width="300" />
					</div>
				</div>
				<div class="center">
					<button type="submit" class="btn btn-primary" name="submit" id="submit">Ajouter</button>
				</div>
			</form>
		</div>
		<div class="errors">
			<c:if test="${fn:length(errors) > 0}">
				<p>Des erreurs ont empêché la création de cette nouvelle idée:</p>
				<ul>
					<c:forEach var="error" items="${errors}">
						<li>${error}</li>
					</c:forEach>
				</ul>
			</c:if>
		</div>
	</jsp:body>
</t:template_body_protected>
</html>