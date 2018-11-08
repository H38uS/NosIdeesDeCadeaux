<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>Création d'un groupe</h2>
		<div>
			<c:if test="${not empty idee}">
				<form action="protected/create_a_group" method="post" >
					<table>
						<input type="hidden" name="idee" value="${idee.id}">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<tr>
							<td><label for="total">Montant minimum (environ) pour ce cadeau, en incluant ta participation</label></td>
							<td><input id="total" name="total" type="text" value="${total}" required="required" /></td>
						</tr>
						<tr>
							<td><label for="amount">Ta participation</label></td>
							<td><input id="amount" name="amount" type="text" value="${amount}" required="required" /></td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<input type="submit" name="submit" id="submit" value="Créer le groupe!" />
							</td>
						</tr>
					</table>
				</form>
				<h2>Rappel de l'idée</h2>
				<t:template_une_idee />
			</c:if>
			<c:if test="${empty idee}">
				L'idée sur laquelle vous souhaitez créer un groupe n'existe pas, ou vous n'avez pas les droits pour le faire.
			</c:if>
		</div>
		<c:if test="${fn:length(errors) > 0}">
			<div class="alert alert-danger">
				<p>Des erreurs ont empêché la création de ce groupe:</p>
				<ul>
					<c:forEach var="error" items="${errors}">
						<li>${error}</li>
					</c:forEach>
				</ul>
			</div>
		</c:if>
	</jsp:body>
</t:normal_protected>