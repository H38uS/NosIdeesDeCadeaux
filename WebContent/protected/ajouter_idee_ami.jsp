<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_proctected>
		<jsp:body>
		<c:if test="${not empty text}">
			Votre idée "${text}" a bien été créé.
		</c:if>
		<h2>Ajouter une nouvelle idée à ${user.name}</h2>
		<div>
			<form action="protected/ajouter_idee_ami?id=${user.id}&${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
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
	</jsp:body>
</t:normal_proctected>