<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>Ajouter une nouvelle idée</h2>
		<div>
			<form action="protected/ma_liste?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
				<table>
					<tr>
						<td><label for="text">Le texte de l'idée</label></td>
						<td><textarea id="text" name="text" cols="70" rows="6"></textarea></td>
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
						<td>
							<label for="addImage" >Ajouter une image</label>
						</td>
						<td>
							<input id="addImage" name="image" type="file" accept="image/jpg, image/jpeg, image/png" />
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="submit" name="submit" id="submit" value="Ajouter" />
						</td>
					</tr>
				</table>
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
		<h2>Ma liste de cadeaux</h2>
		<c:if test="${fn:length(idees) > 0}">
			<table>
				<thead>
					<tr>
						<th>Type</th>
						<th>Idée</th>
						<th>Image</th>
					</tr>
				</thead>
				<c:forEach var="idee" items="${idees}">
					<tr>
						<td>
							<c:if test="${not empty idee.category}">
								<img src="public/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" />
							</c:if>
						</td>
						<td>${idee.html}</td>
						<td>
							<c:if test="${not empty idee.image}">
								<img src="${idee.imageSrcSmall}" width="150" />
							</c:if>
						</td>
						<td>
							<a href="protected/modifier_idee?id=${idee.id}">Modifier</a>
						</td>
						<td>
							<form method="POST" action="protected/remove_an_idea">
								<input type="hidden" name="ideeId" value="${idee.id}">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<input type="submit" name="submit" value="Supprimer">
							</form>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</jsp:body>
</t:normal_protected>