<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_proctected>
		<jsp:body>
		<h2>Informations générales</h2>
		
		<form action="protected/mon_compte" method="post">
			<table>
				<tr>
					<td>Email</td>
					<td>
						<input type="text" name="email" value="${user.email}">
					</td>
				</tr>
				<tr>
					<td>Nom</td>
					<td>
						<input type="text" name="name" value="${user.name}">
					</td>
				</tr>
				<tr>
					<td>Date de naissance</td>
					<td>
						<input type="date" name="birthday" placeholder="aaaa-mm-jj" value="${user.birthday}" title="Utilisez le format suivant: aaaa-mm-jj (année sur 4 chiffres, tiret, mois sur 2 chiffres, tiret, jour sur deux chiffres)" >
					</td>
				</tr>
			</table>
			
			<div class="errors">
				<c:if test="${fn:length(errors_info_gen) > 0}">
					<p>Des erreurs ont empêché la sauvegarde:</p>
					<ul>
						<c:forEach var="error" items="${errors_info_gen}">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</c:if>
			</div>
			
			<input type="hidden" name="modif_info_gen" value="true">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="submit" name="submit" value="Sauvegarder">
		</form>
		
		<h2>Notifications</h2>
		
	</jsp:body>
</t:normal_proctected>