<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:normal>
	<jsp:body>
		<h2>Veuillez vous identifier pour accéder à cette page.</h2>
		<div>
			<form method="POST" action="login">
				<table>
					<tr>						
						<td id="username-label">
							<label for="username" class="required">Identifiant</label>
						</td>
						<td>
							<input type="text" name="j_username" id="username" value="" />
						</td>
					</tr>
					<tr>
						<td id="password-label">
							<label for="password" class="required">Mot de passe</label>
						</td>
						<td>
							<input type="password" name="j_password" id="password" value="" />
						</td>
					</tr>
					<tr>
						<td>
							<label for="remember-me">Maintenir la connexion</label>
						</td>
						<td>
							<input id="remember-me" type="checkbox" name="remember-me" />
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="submit" name="submit" id="submit" value="Se connecter" />
						</td>
					</tr>
				</table>
				
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</div>
		<div>
			Pas encore de compte ? <a href="public/creation_compte.jsp">Créez en un </a>!
		</div>
	</jsp:body>
</t:normal>
