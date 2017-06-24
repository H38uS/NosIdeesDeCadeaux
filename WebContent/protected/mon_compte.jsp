<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_proctected>
		<jsp:body>
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
							<img src="${user.avatarSrcSmall}" width="150" />
							<input type="hidden" name="old_picture" value="${user.avatar}" />
						</td>
					</tr>
				</c:if>
				<tr>
					<td>
						<label for="addImage">Choisissez un nouvel avatar</label>
					</td>
					<td>
						<input id="addImage" name="image" type="file" accept="image/jpg, image/jpeg, image/png" />
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
		
		<h2>Notifications</h2>
		<table>
			<c:forEach var="notif" items="${notif_types}">
				<form action="protected/update_notification_parameter" method="post" >
					<tr>
						<input hidden="true" name="id" value="${notif.id}">
						<input hidden="true" name="name" value="${notif.parameterName}">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<td width="270px">
							<label for="${notif.parameterName}_value">${notif.parameterName}</label>
						</td>
						<td>
							<select id="value" name="value">
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
							<input type="submit" name="submit" value="Sauvegarder">
						</td>
					</tr>
				</form>
			</c:forEach>
		</table>
		
	</jsp:body>
</t:normal_proctected>