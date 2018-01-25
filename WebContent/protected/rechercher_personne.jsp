<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<t:template_head_includes>
	<link rel="stylesheet" type="text/css" href="public/css/common.css" />
	<link rel="stylesheet" type="text/css" href="public/css/normal/normal.css" />
	<script src="public/js/search.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
	<jsp:body>
		<h2>Rechercher une personne pour l'ajouter</h2>
		<div>
			<form method="POST" action="protected/rechercher_personne">
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
							<label id="label_only_non_friend" for="only_non_friend">Afficher uniquement les non-amis</label>
						</td>
						<td>
							<c:if test="${onlyNonFriend}">
								<input type="checkbox" name="only_non_friend" id="only_non_friend" checked="checked" />
								<span id="span_only_non_friend" class="checkbox"></span>
							</c:if>
							<c:if test="${not onlyNonFriend}">
								<input type="checkbox" name="only_non_friend" id="only_non_friend" />
								<span id="span_only_non_friend" class="checkbox"></span>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="submit" name="submit" id="submit" value="Rechercher !" />
						</td>
					</tr>
				</table>
				<input type="hidden" name="page" value="1" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
		</div>
		<div id="res">
			<c:if test="${not empty entities}">
				<c:if test="${not empty pages}">
					<div class="center">
						<c:if test="${current != 1}">
							<a href="protected/rechercher_personne?page=${current-1}&only_non_friend=${onlyNonFriend}&name=${name}">Précédent</a>
						</c:if>
						<c:forEach var="page" items="${pages}">
							<c:choose>
								<c:when test="${current != page.numero}">
									<a href="protected/rechercher_personne?page=${page.numero}&only_non_friend=${onlyNonFriend}&name=${name}">${page.numero}</a>
								</c:when>
								<c:otherwise>
									${page.numero}
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:if test="${current != last}">
						<a href="protected/rechercher_personne?page=${current+1}&only_non_friend=${onlyNonFriend}&name=${name}">Suivant</a>
						</c:if>
					</div>
				</c:if>
				<ul id="person_square_container">
					<c:forEach var="user" items="${entities}">
						<li class="person_square">
							<div class="vertical_center_div">
								<span class="verticalcenter_helper"></span>
								<img class="verticalcenter" src="${avatars}/${user.avatarSrcSmall}">
							</div>
							<div>${user.name}</div>
							<div>${user.email}</div>
							<div style="height:70px;">
								<c:choose>
									<c:when test="${user.isInMyNetwork}">
										<span class="verticalcenter_helper"></span>
										<img class="verticalcenter" alt="${user.name} fait déjà parti de vos amis." title="${user.name} fait déjà parti de vos amis." src="public/image/friend.png">
									</c:when>
									<c:when test="${not empty user.freeComment}">
										<span class="verticalcenter_helper"></span>
										<img class="verticalcenter" alt="${user.freeComment}" title="${user.freeComment}" src="public/image/demande_envoyee.jpg">
									</c:when>
									<c:otherwise>
										<form method="POST" action="protected/demande_rejoindre_reseau">
											<input type="hidden" name="user_id" value="${user.id}" >
											<input type="submit" name="submit" id="submit" value="Envoyer une demande" />
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										</form>
									</c:otherwise>
								</c:choose>
							</div>
						</li>
					</c:forEach>
				</ul>
			</c:if>
			<c:if test="${not empty pages}">
				<div class="center">
					<c:if test="${current != 1}">
						<a href="protected/rechercher_personne?page=${current-1}&only_non_friend=${onlyNonFriend}&name=${name}">Précédent</a>
					</c:if>
					<c:forEach var="page" items="${pages}">
						<c:choose>
							<c:when test="${current != page.numero}">
								<a href="protected/rechercher_personne?page=${page.numero}&only_non_friend=${onlyNonFriend}&name=${name}">${page.numero}</a>
							</c:when>
							<c:otherwise>
								${page.numero}
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<c:if test="${current != last}">
					<a href="protected/rechercher_personne?page=${current+1}&only_non_friend=${onlyNonFriend}&name=${name}">Suivant</a>
					</c:if>
				</div>
			</c:if>
		</div>
	</jsp:body>
</t:template_body_protected>