<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<t:normal_public>
	<jsp:body>
		<div class="default_form">
			<h2 class="fl_yellow">Réinitialisation du mot de passe</h2>
			<div>
				<form method="POST" action="public/changer_mot_de_passe_depuis_reinit">
				
					<div class="form-group">
						<label for="pwd1" class="required fl_green">Nouveau mot de passe</label>
						<c:choose>
							<c:when test="${not empty pwd1_error}">
								<input type="password" name="pwd1" id="pwd1" class="form-control is-invalid" value="" />
							</c:when>
							<c:otherwise>
								<input type="password" name="pwd1" id="pwd1" class="form-control" value="" />
							</c:otherwise>
						</c:choose>
						<div class="invalid-feedback">${pwd1_error[0]}</div>
					</div>

					<div class="form-group">
						<label for="pwd2" class="required fl_blue">Confirmer le mot de passe</label>
						<c:choose>
							<c:when test="${not empty pwd2_error}">
								<input type="password" name="pwd2" class="form-control is-invalid" id="pwd2" value="" />
							</c:when>
							<c:otherwise>
								<input type="password" name="pwd2" class="form-control" id="pwd2" value="" />
							</c:otherwise>
						</c:choose>
						<div class="invalid-feedback">${pwd2_error[0]}</div>
					</div>

					<input type="hidden" name="userIdParam" value="${userIdParam}">
					<input type="hidden" name="tokenId" value="${tokenId}">
					
					<button type="submit" class="btn btn-primary my-3" name="submit" id="submit">Réinitialiser le mot de passe</button>
				</form>
			</div>
		</div>
	</jsp:body>
</t:normal_public>
