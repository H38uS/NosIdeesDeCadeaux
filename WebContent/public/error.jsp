<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:normal_public>
	<jsp:body>
		<div class="login_form">
			<h2 class="fl_yellow">Identification</h2>
			<div>
				<form method="POST" action="login">
					<table>
						<tr>
							<td id="username-label">
								<label for="username" class="required fl_green">Identifiant</label>
							</td>
							<td>
								<input type="text" name="j_username" id="username" value="" />
							</td>
						</tr>
						<tr>
							<td id="password-label">
								<label for="password" class="required fl_purple">Mot de passe</label>
							</td>
							<td>
								<input type="password" name="j_password" id="password" value="" />
							</td>
						</tr>
						<tr>
							<td>
								<label for="remember-me" class="fl_green">Maintenir la connexion</label>
							</td>
							<td>
								<input id="remember-me" type="checkbox" name="remember-me" />
								<span class="checkbox"></span>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<input type="submit" name="submit" id="submit" class="fl_yellow" value="Se connecter" />
							</td>
						</tr>
					</table>
					
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				</form>
			</div>
			<div class="error">
				<p>
					L'identifiant ou le mot de passe est incorrect.
				</p>
			</div>
			<div><br/><br/>Ou...</div>
			<div class="center">
				<a href="public/creation_compte.jsp"><span class="fl_green">Créez</span> un compte !</a>
			</div>
		</div>
	</jsp:body>
</t:normal_public>