<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal_public_recaptcha>
	<jsp:body>
		<div class="default_form">
			<h2 class="fl_yellow">Création de compte</h2>
			<div>
				<form method="POST" action="public/creation_compte">
					
					<div class="form-group">
						<label for="email" class="required fl_green">Adresse email</label>
						<c:choose>
							<c:when test="${fn:length(email_errors) > 0}">
								<input type="email" class="form-control is-invalid" name="email" id="email" value="${param.email}" required aria-describedby="emailHelp" placeholder="Entrer votre email" />
							</c:when>
							<c:otherwise>
								<input type="email" class="form-control" name="email" id="email" value="${param.email}" required aria-describedby="emailHelp" placeholder="Entrer votre email" />
							</c:otherwise>
						</c:choose>
						<small id="emailHelp" class="form-text text-muted">L'email sera votre identifiant pour vous connecter.</small>
						<div class="invalid-feedback">
							<ul>
							<c:forEach var="error" items="${email_errors}">
								<li>${error}</li>
							</c:forEach>
							</ul>
						</div>
					</div>
					
					<div class="form-group">
						<label for="pwd" class="required fl_blue">Mot de passe</label>
						<c:choose>
							<c:when test="${fn:length(pwd_errors) > 0}">
								<input type="password" class="form-control is-invalid" name="pwd" id="pwd" value="${param.pwd}" required />
							</c:when>
							<c:otherwise>
								<input type="password" class="form-control" name="pwd" id="pwd" value="${param.pwd}" required />
							</c:otherwise>
						</c:choose>
						<div class="invalid-feedback">
							<ul>
							<c:forEach var="error" items="${pwd_errors}">
								<li>${error}</li>
							</c:forEach>
							</ul>
						</div>
					</div>

					<div class="form-group">
						<label for="gcaptcha">Cochez la case</label>
						<span id="gcaptcha" class="g-recaptcha" data-sitekey="6Ld2xDoUAAAAAHsDa75FjuB2v6fL1X67IaZ1l1WB"></span>
					</div>

					<div class="form-group">
						<label for="pseudo">Pseudo</label>
						<input name="pseudo" class="form-control" type="text" value="${param.pseudo}" />
					</div>
				
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<button type="submit" class="btn btn-primary" name="submit" id="submit">Créer !</button>
				</form>
			</div>
		</div>
	</jsp:body>
</t:normal_public_recaptcha>