<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>L'idée ${idee.owner.myDName} - <a href="protected/voir_liste?id=${idee.owner.id}">Sa liste</a></h2>
		<t:template_une_idee />
		<h2>Détail du groupe</h2>
		<div>
			<c:if test="${not empty group}">
				<p>Montant total souhaité : ${group.totalAmount}€</p>
				<div>
					<c:choose>
						<c:when test="${empty group.shares}">
							Aucune participation pour le moment.
						</c:when>
						<c:otherwise>
							<ul>
								<c:forEach var="share" items="${group.shares}">
									<li>${share.user.name} : ${share.shareAmount}€
										<td></td>
										
									</li>
								</c:forEach>
							</ul>
							</table>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>
			<c:if test="${empty group}">
				Le groupe n'existe pas ou vous ne pouvez pas intéragir avec.
			</c:if>
		</div>
		<h2>Vos actions</h2>
		<c:choose>
			<c:when test="${not is_in_group}">
				<div>Vous ne participez pas encore à ce groupe.</div>
				<form method="POST" action="protected/detail_du_groupe">
					<input type="hidden" name="groupid" value="${group.id}" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<c:choose>
						<c:when test="${is_mobile}">
							<input name="amount" type="text" value="${share.amount}" />
							<div>
								<input type="submit" name="submit" id="submit" value="Participer !" />
							</div>
						</c:when>
						<c:otherwise>
							<input style="width:130px" name="amount" type="text" value="${share.amount}" />
							<input style="width:130px" type="submit" name="submit" id="submit" value="Participer !" />
						</c:otherwise>
					</c:choose>
				</form>
			</c:when>
			<c:otherwise>
				<c:forEach var="share" items="${group.shares}">
					<c:if test="${userid == share.user.id}">
						<c:choose>
							<c:when test="${is_mobile}">
								<form method="POST" action="protected/detail_du_groupe">
									<input name="amount" type="text" value="${share.shareAmount}" />
									<input type="hidden" name="groupid" value="${group.id}" />
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<div>
										<input type="submit" name="submit" id="submit" value="Modifier !" />
									</div>
								</form>
								<form method="POST" action="protected/detail_du_groupe">
									<input type="hidden" name="amount" value="annulation" />
									<input type="hidden" name="groupid" value="${group.id}" />
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<input type="submit" name="submit" id="submit" value="Annuler ma participation" />
								</form>
							</c:when>
							<c:otherwise>
								<form style="display:inline" method="POST" action="protected/detail_du_groupe">
									Modifier le montant : 
									<input style="display:inline-block;width:130px" name="amount" type="text" value="${share.shareAmount}" />
									<input type="hidden" name="groupid" value="${group.id}" />
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<input style="width:130px" type="submit" name="submit" id="submit" value="Modifier !" />
								</form>
								<form style="display:inline-block" method="POST" action="protected/detail_du_groupe">
									<input type="hidden" name="amount" value="annulation" />
									<input type="hidden" name="groupid" value="${group.id}" />
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<input type="submit" name="submit" id="submit" value="Annuler ma participation" />
								</form>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:forEach>
			</c:otherwise>
		</c:choose>
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
		<c:if test="${group.total > currentTotal}">
			<h3>Suggérer ce groupe à quelqu'un</h3>
			Il manque un peu (${remaining}€ très exactement)... N'hésitez plus, <a href="protected/suggerer_groupe_idee?groupid=${group.id}">suggérer</a> ce groupe à d'autres personnes !
		</c:if>
	</jsp:body>
</t:normal_protected>