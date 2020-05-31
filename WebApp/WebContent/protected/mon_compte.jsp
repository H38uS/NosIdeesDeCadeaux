<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
	<script src="resources/js/mon_compte.js" type="text/javascript"></script>
	<c:if test="${not is_mobile}">
		<script src="resources/js/browser/pictures.js" type="text/javascript"></script>
	</c:if>
</t:template_head_includes>
<t:template_body_protected>
	<jsp:body>
	
	<div class="default_form">
		<h2>Informations générales</h2>
	
		<form id="form_main_change_mon_compte" action="protected/mon_compte?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">

			<div class="form-group">
				<label for="email">Email</label>
				<input id="email" class="form-control" type="text" name="email" value="${user.email}">
			</div>

			<div class="form-group">
				<label for="name">Nom</label>
				<input id="name" type="text" class="form-control" name="name" value="${user.name}">
			</div>

			<div class="form-group">
				<label for="birthday">Date de naissance</label>
				<input id="birthday" 
						class="form-control"
						type="date"
						name="birthday"
						placeholder="aaaa-mm-jj"
						value="${user.birthday}"
						title="Utilisez le format suivant: aaaa-mm-jj (année sur 4 chiffres, tiret, mois sur 2 chiffres, tiret, jour sur deux chiffres)">
			</div>

			<c:if test="${not empty user.avatar}">
				<div class="form-group">
					<label>Avatar actuel</label>
					<a id="avatar_picture" href="${avatars}/${user.avatarSrcLarge}" class="thickbox img">
						<img src="${avatars}/${user.avatarSrcSmall}" width="150" class="input" />
					</a>
					<input type="hidden" name="old_picture" id="old_picture" value="${user.avatar}" />
				</div>
			</c:if>
			
			<div class="form-group">
				<input id="imageFile" name="image" type="file" accept="image/jpg, image/jpeg, image/png" />
				<div>
					<label for="imageFile" class="btn btn-light">Choisissez un nouvel avatar</label>
				</div>
				<span class="d-none d-md-inline-block pb-2">Fichier Choisi: </span>
				<span id="newImage" class="picture_not_drag input"></span>
				<div class="center">
					<img id="imageFilePreview" alt="" src="" width="300" />
				</div>
			</div>
			
			<div class="form-group">
				<label for="new_password">Nouveau mot de passe</label>
				<input class="form-control" type="password" name="new_password" id="new_password" value="" />
			</div>

			<div class="form-group">
				<label for="conf_password">Confirmation du mot de passe</label>
				<input class="form-control" type="password" name="conf_password" id="conf_password" value="" />
			</div>
			
			<c:if test="${fn:length(errors_info_gen) > 0}">
				<div class="alert alert-danger">
					<p>Des erreurs ont empêché la sauvegarde:</p>
					<ul>
						<c:forEach var="error" items="${errors_info_gen}">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</div>
			</c:if>
			
			<input type="hidden" name="modif_info_gen" value="true">
			
			<div class="center">
				<button class="btn btn-primary" id="submit_main_form" type="submit">Sauvegarder</button>
			</div>
		</form>
	</div>
	<c:if test="${sauvegarde_ok}">
		Mise à jour effectuée avec succès.
	</c:if>
	
	<h2 class="pt-3">Contrôle parental - Procuration</h2>

	<div class="border border-info bg-light rounded mb-2 p-2">
		<div class="row align-items-center ">
			<div class="col-auto">
				<h3>Mes comptes parent</h3>
			</div>
			<div class="col-auto">
				<c:if test="${not empty parents}">
					<form id="supprimer_parents" class="form-inline" method="POST" action="protected/supprimer_parents">
						<button type="submit" class="btn btn-primary">Je suis assez grand(e), les supprimer</button>
					</form>
				</c:if>
			</div>
		</div>
		<c:choose>
			<c:when test="${not empty parents}">
				<ul id="parent_names_list">
					<c:forEach var="parent" items="${parents}">
					<li>${parent}</li>
					</c:forEach>
				</ul>
			</c:when>
			<c:otherwise>
				<div class="alert alert-info">Vous n'avez actuellement pas de comptes parent.</div>
			</c:otherwise>
		</c:choose>
		<form id="ajouter_un_parent" class="form-inline" method="POST" action="">
			<div class="row align-items-center justify-content-center">
				<div class="col-auto">
					<label for="input_add_parent" class="d-none d-lg-inline-block mr-3">Ajouter un nouveau parent pour gérer ce compte</label>
				</div>
				<div class="col-auto px-0">
					<input type="text" class="form-control" name="name" id="input_add_parent" placeholder="Nom ou email du parent" />
				</div>
				<div class="col-auto">
					<button id="btn_add_parent" class="btn btn-primary mx-md-2" type="submit">Ajouter</button>
				</div>
			</div>
		</form>
		<h3 id="mes_comptes_enfants_h3" class="mt-2">Mes comptes enfant</h3>
		<c:choose>
			<c:when test="${not empty children}">
			<table>
				<c:forEach var="child" items="${children}">
				<tr>
					<td>${child}</td>
					<td>
						<form method="POST" action="protected/connexion_enfant">
							<input type="hidden" name="name" value="${child.id}" />
							<button type="submit" class="btn btn-primary ml-sm-2">Se connecter avec ce compte</button>
						</form>
					</td>
				</tr>
				</c:forEach>
			</table>
			</c:when>
			<c:otherwise>
				Vous n'avez actuellement pas de comptes enfant.
			</c:otherwise>
		</c:choose>
		<p class="mb-0">
			Pour en ajouter, connectez-vous (ou créez un autre compte) depuis le compte enfant
			afin d'initialiser la procuration. <br/>Vous pourrez alors accéder à vos comptes enfant depuis le vôtre !
		</p>
	</div>

	<h2 class="pt-3">Type de notifications</h2>
	
	<div class="container">
	
	<c:forEach var="notif" items="${notif_types}">
		<div class="border border-info bg-light rounded mb-2 p-2">
			<div class="row align-items-center py-2">
				<form class="form-inline w-100" action="protected/update_notification_parameter" method="post">
					<div class="col-12 col-md-5 col-lg-7">
						<label class="justify-content-start" for="${notif.parameterName}_value">${notif.parameterDescription}</label>
					</div>
					<div class="col-12 col-sm-7 col-md-auto">
						<select class="form-control w-100" id="value_${notif.parameterName}" name="value">
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
					</div>
					<input type="hidden" name="id" value="${notif.id}">
					<input type="hidden" name="name" value="${notif.parameterName}">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="center col-12 mt-2 col-sm-5 mt-sm-0 col-md-auto">
						<button class="btn btn-primary notification_form_submit" type="submit">Sauvegarder</button>
					</div>
				</form>
			</div>
		</div>
	</c:forEach>
	</div>
	
</jsp:body>
</t:template_body_protected>
</html>
