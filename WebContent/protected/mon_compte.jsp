<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		
		<div class="login_form">
			<h2>Informations générales</h2>
		
			<form action="protected/mon_compte?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
				<table>
					<tr>
						<td>Email</td>
						<td>
							<input type="text" name="email" value="${user.email}">
						</td>
					</tr>
					<tr>
						<td>Nom</td>
						<td>
							<input type="text" name="name" value="${user.name}">
						</td>
					</tr>
					<tr>
						<td>Date de naissance</td>
						<td>
							<input type="date" name="birthday" placeholder="aaaa-mm-jj" value="${user.birthday}"
													title="Utilisez le format suivant: aaaa-mm-jj (année sur 4 chiffres, tiret, mois sur 2 chiffres, tiret, jour sur deux chiffres)">
						</td>
					</tr>
					<c:if test="${not empty user.avatar}">
						<tr>
							<td>Avatar actuel</td>
							<td>
								<img src="${avatars}/${user.avatarSrcSmall}" width="150" class="input" />
								<input type="hidden" name="old_picture" value="${user.avatar}" />
							</td>
						</tr>
					</c:if>
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
							<label for="imageFile" class="custom-file-upload" >Choisissez un nouvel avatar</label>
						</td>
					</tr>
					<tr>
						<td>Nouveau mot de passe</td>
						<td>
							<input type="password" name="new_password" id="new_password" value="" />
						</td>
					</tr>
					<tr>
						<td>Confirmation du mot de passe</td>
						<td>
							<input type="password" name="conf_password" id="conf_password" value="" />
						</td>
					</tr>
				</table>
				
				<div class="errors">
					<c:if test="${fn:length(errors_info_gen) > 0}">
						<p>Des erreurs ont empêché la sauvegarde:</p>
						<ul>
							<c:forEach var="error" items="${errors_info_gen}">
								<li>${error}</li>
							</c:forEach>
						</ul>
					</c:if>
				</div>
				
				<input type="hidden" name="modif_info_gen" value="true">
				<input type="submit" name="submit" value="Sauvegarder">
			</form>
		</div>
		<c:if test="${sauvegarde_ok}">
			Mise à jour effectuée avec succès.
		</c:if>
		
		<h2>Type de notifications</h2>
		<table>
			<c:forEach var="notif" items="${notif_types}">
				<form action="protected/update_notification_parameter" method="post" >
					<tr>
						<td>
							<label for="${notif.parameterName}_value">${notif.parameterDescription}</label>
						</td>
						<td style="padding:0 20px;" >
							<select id="value_${notif.parameterName}" name="value">
								<c:forEach var="value" items="${possible_values}">
									<c:choose>
										<c:when test="${value == notif.parameterValue}">
											<option selected="selected" value="${value}">${value}</option>
										</c:when>
										<c:otherwise>
											<option value="${value}">${value}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</td>
						<td>
							<input hidden="true" name="id" value="${notif.id}">
							<input hidden="true" name="name" value="${notif.parameterName}">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="submit" name="submit" value="Sauvegarder">
						</td>
					</tr>
				</form>
			</c:forEach>
		</table>
		
	</jsp:body>
</t:normal_protected>