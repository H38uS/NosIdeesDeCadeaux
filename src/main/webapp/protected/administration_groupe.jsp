<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal>
	<jsp:body>
		<h2>Demandes reçues</h2>
		<div>
			<c:choose>
				<c:when test="${fn:length(demandes) > 0}">
					<form method="POST" action="protected/administration_groupe">
						<table>
							<thead>
								<tr>
									<th>Nom du demandeur</th>
								</tr>
							</thead>
							<c:forEach var="demande" items="${demandes}">
								<tr>
									<td>${demande.name}</td>
									<td>
										<label for="acc_choix_${demande.id}">Accepter</label>
										<input type="radio" id="acc_choix_${demande.id}" name="choix_${demande.id}" value="Accepter">
									</td>
									<td>
										<label for="ref_choix_${demande.id}">Refuser</label>
										<input type="radio" id="ref_choix_${demande.id}" name="choix_${demande.id}" value="Refuser">
									</td>
								</tr>
							</c:forEach>
						</table>
						<input type="hidden" name="groupId" id="groupId" value="${groupId}" >
						<input type="submit" id="submit" name="submit" value="Sauvegarder">
					</form>
				</c:when>
				<c:otherwise>
					Aucune demande reçue pour le moment.
				</c:otherwise>
			</c:choose>			
		</div>
		<h2>Membres de votre groupe</h2>
		<div>
			<table>
				<thead>
					<tr>
						<th>Nom du membre</th>
					</tr>
				</thead>
			<c:forEach var="member" items="${members}">
				<tr>
					<td>${member.name}</td>
				</tr>
			</c:forEach>
			</table>
		</div>
	</jsp:body>
</t:normal>