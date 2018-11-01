<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<t:normal_public>
	<jsp:body>
		<div class="default_form">
			<h2 class="fl_yellow">Réinitialisation du mot de passe</h2>
			<div>
				<form method="POST" action="public/reinitialiser_mot_de_passe">

					<div class="form-group">
						<label for="email1" class="required fl_green">Adresse email</label>
						<c:choose>
							<c:when test="${not empty email1_error}">
								<input type="text" name="email1" id="email1" value="${email1}" class="form-control is-invalid" />
							</c:when>
							<c:otherwise>
								<input type="text" name="email1" id="email1" value="${email1}" class="form-control" />
							</c:otherwise>
						</c:choose>
						<div class="invalid-feedback">${email1_error[0]}</div>
					</div>

					<div class="form-group">
						<label for="email2" class="required fl_green">Confirmer l'adresse email</label>
						<c:choose>
							<c:when test="${not empty email2_error}">
								<input type="text" name="email2" id="email2" value="${email2}" class="form-control is-invalid" />
							</c:when>
							<c:otherwise>
								<input type="text" name="email2" id="email2" value="${email2}" class="form-control" />
							</c:otherwise>
						</c:choose>
						<div class="invalid-feedback">${email2_error[0]}</div>
					</div>

					<button type="submit" class="btn btn-primary my-3" name="submit" id="submit">Réinitialiser le mot de passe</button>
				</form>
			</div>
		</div>
	</jsp:body>
</t:normal_public>
