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
			<script src="resources/js/mobile/idea.js" type="text/javascript"></script>
		</c:when>
	</c:choose>
</t:template_head_includes>
<t:template_body_protected>
	<jsp:body>
		Avant d'ajouter une idée, je voudrai consulter <a href="protected/voir_liste?id=${userid}">ma liste</a>.
		<h2>Ajouter une nouvelle idée</h2>
		<div>
			<c:choose>
				<c:when test="${is_mobile}">
					<form action="protected/ma_liste?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
						<textarea id="text" name="text" cols="70" rows="6" placeholder="Tapez ici le texte de votre idée !"></textarea>
						<select id="type" name="type">
							<option value="">Sélectionnez un type</option>
							<c:forEach var="type" items="${types}">
								<option value="${type.name}">${type.alt}</option>
							</c:forEach>
							<option value="">Autre</option>
						</select>
						<select id="priority" name="priority">
							<option value="1">Sélectionnez une priorité</option>
							<c:forEach var="priorite" items="${priorites}">
								<option value="${priorite.id}">${priorite.name}</option>
							</c:forEach>
						</select>
						<input id="imageFile" name="image" type="file" accept="image/jpg, image/jpeg, image/png" />
						<label for="imageFile" class="custom-file-upload">Ajouter une image</label>
						<span id="newImage" class="input"></span><br/>
						<div class="center">
							<img id="imageFilePreview" alt="" src="" width="400" />
						</div>
						<input type="submit" name="submit" id="submit" value="Ajouter" />
					</form>
				</c:when>
				<c:otherwise>
					<form action="protected/ma_liste?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
						<table id="ma_liste_table_ajouter">
							<tr>
								<td><label for="text">Le texte de l'idée</label></td>
								<td><textarea id="text" name="text" cols="70" rows="6" placeholder="Tapez ici le texte de votre idée !"></textarea></td>
							</tr>
							<tr>
								<td><label for="type">Type</label></td>
								<td>
									<select id="type" name="type">
										<option value="">Sélectionnez un type</option>
										<c:forEach var="type" items="${types}">
											<option value="${type.name}">${type.alt}</option>
										</c:forEach>
										<option value="">Autre</option>
									</select>
								</td>
							</tr>
							<tr>
								<td><label for="priority">Priorité</label></td>
								<td>
								<select id="priority" name="priority">
									<option value="1">Sélectionnez une priorité</option>
									<c:forEach var="priorite" items="${priorites}">
										<option value="${priorite.id}">${priorite.name}</option>
									</c:forEach>
								</select>
								</td>
							</tr>
							<tr>
								<td>Fichier Choisi</td>
								<td>
									<span id="newImage" class="input"></span>
								</td>
							</tr>
							<tr>
								<td>
									<input id="imageFile" name="image" type="file" accept="image/jpg, image/jpeg, image/png" />
								</td>
								<td>
									<label for="imageFile" class="custom-file-upload">Ajouter une image</label>
								</td>
							</tr>
							<tr>
								<td></td>
								<td>
									<img id="imageFilePreview" alt="" src="" width="300" />
								</td>
							</tr>
							<tr>
								<td colspan="2" align="center">
									<input type="submit" name="submit" id="submit" value="Ajouter" />
								</td>
							</tr>
						</table>
					</form>
				</c:otherwise>
			</c:choose>
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