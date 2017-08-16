<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<t:normal_public>
	<jsp:body>
		<div class="login_form">
			<h2 class="fl_yellow">Réinitialisation du mot de passe</h2>
			<div>
				<form method="POST" action="public/reinitialiser_mot_de_passe">
					<table>
						<tr>
							<td>
								<label for="email1" class="required fl_green">Adresse email</label>
							</td>
							<td>
								<input type="text" name="email1" id="email1" value="${email1}" />
							</td>
						</tr>
						<c:if test="${not empty email1_error}">
						<tr>
							<td class="error" colspan="2">${email1_error[0]}</td>
						</tr>
						</c:if>
						<tr>
							<td>
								<label for="email2" class="required fl_blue">Confirmer l'adresse email</label>
							</td>
							<td>
								<input type="text" name="email2" id="email2" value="${email2}" />
							</td>
						</tr>
						<c:if test="${not empty email2_error}">
						<tr>
							<td class="error" colspan="2">${email2_error[0]}</td>
						</tr>
						</c:if>
						<tr>
							<td colspan="2" align="center">
								<input type="submit" name="submit" id="submit" class="fl_yellow" value="Réinitialiser le mot de passe" />
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</jsp:body>
</t:normal_public>
