<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>Détail du groupe</h2>
		<div>
			<c:if test="${not empty group}">
				<div>Montant total souhaité : ${group.total}</div>
				<c:choose>
					<c:when test="${empty group.shares}">
						Aucune participation pour le moment.
					</c:when>
					<c:otherwise>
						<table>
							<caption>
								<th>Participant</th>
								<th>Montant</th>
							</caption>
							<c:forEach var="share" items="${group.shares}">
								<tr>
									<td>${share.user.name}</td>
									<c:choose>
										<c:when test="${userid == share.user.id}">
											<td>
												<form method="POST" action="protected/detail_du_groupe">
													<input name="amount" value="${share.amount}" />
													<input type="hidden" name="groupid" value="${group.id}" />
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<input type="submit" name="submit" id="submit" value="Modifier !" />
												</form>
											</td>
											<td>
												<form method="POST" action="protected/detail_du_groupe">
													<input type="hidden" name="amount" value="annulation" />
													<input type="hidden" name="groupid" value="${group.id}" />
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<input type="submit" name="submit" id="submit" value="Annuler ma participation" />
												</form>
											</td>
										</c:when>
										<c:otherwise>
											<td>${share.amount}</td>
										</c:otherwise>
									</c:choose>
								</tr>
							</c:forEach>
						</table>
					</c:otherwise>
				</c:choose>
				<c:if test="${not is_in_group}">
					<div>
						Vous ne participez pas encore à ce groupe !
						Ajouter un montant : 
						<form method="POST" action="protected/detail_du_groupe">
							<input name="amount" value="${share.amount}" />
							<input type="hidden" name="groupid" value="${group.id}" />
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="submit" name="submit" id="submit" value="Participer !" />
						</form>
					</div>
				</c:if>
			</c:if>
			<c:if test="${empty group}">
				Le groupe n'existe pas ou vous ne pouvez pas intéragir avec.
			</c:if>
		</div>
		<div class="errors">
			<c:if test="${fn:length(errors) > 0}">
				<p>Des erreurs sont survenues:</p>
				<ul>
					<c:forEach var="error" items="${errors}">
						<li>${error}</li>
					</c:forEach>
				</ul>
			</c:if>
		</div>
		<h3>Suggérer ce groupe à quelqu'un</h3>
		Il manque un peu... N'hésitez plus, <a href="protected/suggerer_groupe_idee?groupid=${group.id}">suggérer</a> ce groupe à d'autres personnes !
	</jsp:body>
</t:normal_protected>