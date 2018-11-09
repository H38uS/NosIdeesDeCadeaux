<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
	<script src="resources/js/admin.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
	<jsp:body>
		<div class="container">
			<c:forEach var="user" items="${users}">
				<div class="row my-4 align-items-center">
					<div class="col-12 col-lg-6">
						${user}
					</div>
					<div class="col-auto">
						<form method="POST" action="protected/connexion_enfant">
							<input type="hidden" name="name" value="${user.id}" />
							<button class="btn btn-primary" type="submit">Se connecter avec ce compte</button>
						</form>
					</div>
					<div class="col-auto">
						<form class="form_suppression_compte" method="POST" action="administration/suppression_compte">
							<input type="hidden" name="name" value="${user.id}" />
							<button class="btn btn-primary form_suppression_compte_submit" type="submit">Supprimer ce compte</button>
						</form>
					</div>
				</div>
			</c:forEach>
		</div>
	</jsp:body>
</t:template_body_protected>