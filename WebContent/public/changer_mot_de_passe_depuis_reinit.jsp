<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<t:normal_public>
	<jsp:body>
		<div class="login_form">
			<h2 class="fl_yellow">Réinitialisation du mot de passe</h2>
			<div>
				<form method="POST" action="public/changer_mot_de_passe_depuis_reinit">
					<table>
						<tr>
							<td>
								<label for="pwd1" class="required fl_green">Nouveau mot de passe</label>
							</td>
							<td>
								<input type="password" name="pwd1" id="pwd1" value="" />
							</td>
						</tr>
						<c:if test="${not empty pwd1_error}">
						<tr>
							<td class="error" colspan="2">${pwd1_error[0]}</td>
						</tr>
						</c:if>
						<tr>
							<td>
								<label for="pwd2" class="required fl_blue">Confirmer le mot de passe</label>
							</td>
							<td>
								<input type="password" name="pwd2" id="pwd2" value="" />
							</td>
						</tr>
						<c:if test="${not empty pwd2_error}">
						<tr>
							<td class="error" colspan="2">${pwd2_error[0]}</td>
						</tr>
						</c:if>
						<tr>
							<td colspan="2" align="center">
								<input type="submit" name="submit" id="submit" class="fl_yellow" value="Réinitialiser le mot de passe" />
							</td>
						</tr>
					</table>
					<input type="hidden" name="userIdParam" value="${userIdParam}" >
					<input type="hidden" name="tokenId" value="${tokenId}" >
				</form>
			</div>
		</div>
	</jsp:body>
</t:normal_public>
