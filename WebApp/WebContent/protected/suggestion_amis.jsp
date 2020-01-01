<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_protected>
	<jsp:body>
		<c:if test="${not empty error_messages}">
			Des erreurs sont survenues...
			<ul>
				<c:forEach var="message" items="${error_messages}">
					<li>${message}</li>
				</c:forEach>
			</ul>
		</c:if>
		<h2>Suggestions de vos amis</h2>
		<c:choose>
			<c:when test="${not empty suggestions}">
				<form method="POST" action="protected/suggestion_amis">
					<c:forEach var="suggestion" items="${suggestions}">
						<h3>De la part de ${suggestion.suggestedBy.name}</h3>
							<table>
								<thead>
									<tr>
										<th>Nom</th>
										<th>Email</th>
										<th>Envoyer une demande</th>
										<th>Ne rien faire</th>
									</tr>
								</thead>
								<c:forEach var="suggestedUser" items="${suggestion.suggestions}">
									<tr>
										<td>
											<label for="selected_${suggestedUser.id}" >${suggestedUser.name}</label>
										</td>
										<td>
											<label for="selected_${suggestedUser.id}" >${suggestedUser.email}</label>
										</td>
										<td class="center">
											<input type="checkbox" name="selected_${suggestedUser.id}" id="selected_${suggestedUser.id}" />
											<span class="checkbox"></span>
										</td>
										<td class="center">
											<input type="checkbox" name="reject_${suggestedUser.id}" id="reject_${suggestedUser.id}" />
											<span class="checkbox"></span>
										</td>
									</tr>
								</c:forEach>
							</table>
					</c:forEach>
					<input type="submit" id="submit" name="submit" value="Sauvegarder">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				</form>
			</c:when>
			<c:otherwise>
				Vous n'avez reçu aucune suggestion pour le moment.
			</c:otherwise>
		</c:choose>
	</jsp:body>
</t:normal_protected>