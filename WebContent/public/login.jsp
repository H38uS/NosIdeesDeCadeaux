<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:normallight>
	<jsp:body>
		<h2>Veuillez vous identifier pour accéder à cette page.</h2>
		<div class="zendForm">
			<form method="POST" action="j_security_check">
				<table class="zend_form">
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
						<td colspan="2" align="center">
							<input type="submit" name="submit" id="submit" value="Se connecter" />
						</td>
					</tr>
				</table>
			</form>
		</div>
	</jsp:body>
</t:normallight>