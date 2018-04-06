<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>L'idée</h2>
		<ul class="ideas_square_container">
			<li class="idea_square top_tooltip ${idee.displayClass}">
			<div>
				<div class="left">
					<span>${idee.priorite.image}</span>
					<c:if test="${not empty idee.category}">
						<img src="resources/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" width="${action_img_width}px" />
					</c:if>
					<c:if test="${idee.hasComment()}">
						<a href="protected/idee_commentaires?idee=${idee.id}">
							<img src="resources/image/commentaires.png" title="Il existe des commentaires sur cette idée" width="${action_img_width}px" />
						</a>
					</c:if>
					<c:if test="${idee.hasQuestion()}">
						<a href="protected/idee_questions?idee=${idee.id}">
							<img src="resources/image/questions.png" title="Il existe des questions/réponses sur cette idée" width="${action_img_width}px" />
						</a>
					</c:if>
					<span class="outer_top_tooltiptext">
						<span class="top_tooltiptext">
							<c:if test="${empty idee.surpriseBy}">
								<a href="protected/est_a_jour?idee=${idee.id}&from=/${identic_call_back}" class="img">
									<img src="resources/image/a_jour.png" class="clickable" title="Demander si c'est à jour." width="${action_img_width}px" />
								</a>
								<a href="protected/idee_questions?idee=${idee.id}" class="img">
									<img src="resources/image/questions.png" class="clickable" title="Poser une question à ${user.name} / voir les existantes" width="${action_img_width}px" />
								</a>
							</c:if>
							<a href="protected/idee_commentaires?idee=${idee.id}" class="img">
								<img src="resources/image/commentaires.png" title="Ajouter un commentaire / voir les existants" width="${action_img_width}px" />
							</a>
						</span>
					</span>
				</div>
				<div class="left idea_square_text">
					${idee.html}
				</div>
				<c:if test="${not empty idee.image}">
					<div>
						<a href="${ideas_pictures}/${idee.imageSrcLarge}" class="thickbox img" >
							<img src="${ideas_pictures}/${idee.imageSrcSmall}" width="150" />
						</a>
					</div>
				</c:if>
			</div>
			</li>
		</ul>
		<h2>Détail du groupe</h2>
		<div>
			<c:if test="${not empty group}">
				<p>Montant total souhaité : ${group.total}</p>
				<div>
					<c:choose>
						<c:when test="${empty group.shares}">
							Aucune participation pour le moment.
						</c:when>
						<c:otherwise>
							<table>
								<caption>
									<th>Participant</th>
									<th colspan="2" >Montant - Actions</th>
								</caption>
								<c:forEach var="share" items="${group.shares}">
									<tr>
										<td>${share.user.name}</td>
										<c:choose>
											<c:when test="${userid == share.user.id}">
												<td class="left_pad_center">
													<form method="POST" action="protected/detail_du_groupe">
														<input name="amount" value="${share.amount}" />
														<input type="hidden" name="groupid" value="${group.id}" />
														<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
														<input style="width:130px" type="submit" name="submit" id="submit" value="Modifier !" />
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
												<td class="left_pad_center">${share.amount}€</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
								<c:if test="${not is_in_group}">
									<tr>
										<td>
											${username}
										</td>
										<td class="left_pad_center">
											<form method="POST" action="protected/detail_du_groupe">
												<input name="amount" value="${share.amount}" />
												<input type="hidden" name="groupid" value="${group.id}" />
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
												<input style="width:130px" type="submit" name="submit" id="submit" value="Participer !" />
											</form>
										</td>
									</tr>
								</c:if>
							</table>
						</c:otherwise>
					</c:choose>
				</div>
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
		<c:if test="${group.total > currentTotal}">
			<h3>Suggérer ce groupe à quelqu'un</h3>
			Il manque un peu (${group.total - currentTotal}€ très exactement)... N'hésitez plus, <a href="protected/suggerer_groupe_idee?groupid=${group.id}">suggérer</a> ce groupe à d'autres personnes !
		</c:if>
	</jsp:body>
</t:normal_protected>