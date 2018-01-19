<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal_protected>
	<jsp:body>
		<h2>Rechercher des personnes à suggérer à ${user.name}</h2>
		<div>
			<form method="POST" action="protected/suggerer_relations">
				<table>
					<tr>
						<td>
							<label for="name" class="required">Nom / Email de la personne</label>
						</td>
						<td>
							<input type="text" name="name" id="name" value="${name}" />
						</td>
					</tr>
					<tr>
						<td>
							<input type="hidden" name="id" value="${user.id}" >
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="submit" name="submit" id="submit" value="Rechercher !" />
						</td>
					</tr>
				</table>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</div>
		<div>
			<c:if test="${not empty users}">
				<form method="POST" action="protected/suggestion_rejoindre_reseau">
					<table>
						<thead>
							<tr>
								<th>Nom de la personne</th>
								<th>Email</th>
								<th></th>
							</tr>
						</thead>
					<c:forEach var="suggested_user" items="${users}">
						<tr>
							<td>
								<label for="selected_${suggested_user.id}" >${suggested_user.name}</label>
							</td>
							<td>
								<label for="selected_${suggested_user.id}" >${suggested_user.email}</label>
							</td>
							<td>
								<c:if test="${empty suggested_user.freeComment}">
									<input type="checkbox" name="selected_${suggested_user.id}" id="selected_${suggested_user.id}" />
									<span class="checkbox"></span>
								</c:if>
								${suggested_user.freeComment}
							</td>
						</tr>
					</c:forEach>
						<tr>
							<td colspan="2" >
								<input type="submit" name="submit" id="submit" value="Envoyer les suggestions" />
							</td>
						</tr>
					</table>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="hidden" name="userId" value="${user.id}" />
				</form>
			</c:if>
			<c:if test="${empty users and not empty name}">
				Aucune personne trouvée.
			</c:if>
		</div>
	</jsp:body>
</t:normal_protected>