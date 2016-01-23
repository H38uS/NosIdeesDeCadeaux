<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:normal>
	<jsp:body>
		<h2>Veuillez vous identifier pour accéder à cette page.</h2>
		<div>
			<form method="POST" action="j_security_check">
				<table>
					<tr>						
						<td id="username-label">
							<label for="username" class="required">Identifiant</label>
						</td>
						<td>
							<input type="text" name="j_username" id="username" value="${param.j_username}" />
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
		<div class="error">
			<p>
				L'identifiant ou le mot de passe est incorrect.
			</p>
		</div>
	</jsp:body>
</t:normal>