<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal_protected>
	<jsp:body>
		<h2>Erreur</h2>
		<div class="alert alert-danger">
			Des erreurs sont survenues, empêchant d'envoyer les demandes... Veuillez réessayer.
		</div>
		<div>Envoyer de nouvelles demandes : </div>
		<div>
			<form method="POST" class="form-inline" action="protected/suggerer_relations">
				<div class="row align-items-center mx-0">
					<div class="col-auto d-none d-md-inline-block">
						<label for="name">Nom / Email de la personne</label>
					</div>
					<div class="col-7 col-md-auto mx-0">
						<input class="form-control" type="text" name="name" id="name" />
					</div>
					<div class="col-5 col-md-auto">
						<button class="btn btn-primary" type="submit" name="submit" id="submit">Rechercher !</button>
					</div>
				</div>
				<div></div>
				<input type="hidden" name="id" value="${user.id}" >
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</div>
	</jsp:body>
</t:normal_protected>