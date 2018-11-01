<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:normal_public>
	<jsp:body>
		<div class="default_form">
			<h2 class="fl_yellow">Identification</h2>
			<div>
				<form method="POST" action="login">
					<div class="form-group">
						<label for="username" class="required fl_green">Identifiant</label>
						<input type="text" class="form-control is-invalid" name="j_username" id="username" value="" />
					</div>

					<div class="form-group">
						<label for="password" class="required fl_purple">Mot de passe</label>
						<input type="password" name="j_password" class="form-control is-invalid" id="password" value="" />
						<small>
							<a href="public/reinitialiser_mot_de_passe">Mot de passe oublié ?</a>
						</small>
						<div class="invalid-feedback">
							L'identifiant ou le mot de passe est incorrect.
						</div>
					</div>

					<div class="form-check">
						<input id="remember-me" class="form-check-input" type="checkbox" name="remember-me" />
						<label for="remember-me" class="fl_green">Maintenir la connexion</label>
					</div>
				
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<button type="submit" class="btn btn-primary my-3" name="submit" id="submit">Se connecter</button>
					ou...
					<a class="btn btn-secondary my-3" href="public/creation_compte.jsp"><span class="fl_green">Créez</span> un compte !</a>
				</form>
			</div>
		</div>
	</jsp:body>
</t:normal_public>