<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>Modification d'idée</h2>
		<div>
			<c:if test="${not empty idea}">
				<form action="protected/modifier_idee?id=${idea.id}&${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
					<table>
						<tr>
							<td><label for="text">Le texte de l'idée</label></td>
							<td><textarea id="text" name="text" cols="70" rows="6">${idea.text}</textarea></td>
						</tr>
						<tr>
							<td><label for="type">Type</label></td>
							<td>
								<select id="type" name="type">
									<option value="">Sélectionnez un type</option>
									<c:forEach var="type" items="${types}">
										<c:choose>
											<c:when test="${type.name == idea.type}">
												<option value="${type.name}" selected="selected">${type.alt}</option>
											</c:when>
											<c:otherwise>
												<option value="${type.name}">${type.alt}</option>
											</c:otherwise>
										</c:choose>
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
									<c:choose>
										<c:when test="${priorite.id == idea.priorite.id}">
											<option value="${priorite.id}" selected="selected">${priorite.name}</option>
										</c:when>
										<c:otherwise>
											<option value="${priorite.id}">${priorite.name}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
							</td>
						</tr>
						<tr>
							<td>Fichier Choisi</td>
							<td>
								<span id="newImage" class="input" ></span>
							</td>
						</tr>
						<tr>
							<td>
								<input id="imageFile" name="image" type="file" accept="image/jpg, image/jpeg, image/png" />
							</td>
							<td>
								<label for="imageFile" class="custom-file-upload" >Choisissez une nouvelle image</label>
							</td>
						</tr>
						<c:if test="${not empty idea.image}">
							<tr>
								<td>Image actuelle</td>
								<td>
									<img class="form_img" src="${ideas_pictures}/${idea.imageSrcSmall}" width="150" />
									<input type="hidden" name="old_picture" value="${idea.image}" />
								</td>
							</tr>
						</c:if>
						<tr>
							<td colspan="2" align="center">
								<input type="submit" name="submit" id="submit" value="Modifier" />
							</td>
						</tr>
					</table>
				</form>
			</c:if>
			<c:if test="${empty idea}">
				L'idée que vous souhaitez modifier n'existe pas, ou vous n'avez pas les droits pour modifier celle-ci.
			</c:if>
		</div>
		<div class="errors">
			<c:if test="${fn:length(errors) > 0}">
				<p>Des erreurs ont empêché la modification de cette idée:</p>
				<ul>
					<c:forEach var="error" items="${errors}">
						<li>${error}</li>
					</c:forEach>
				</ul>
			</c:if>
		</div>
	</jsp:body>
</t:normal_protected>