<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

			<c:if test="${not empty users}">
				<ul id="person_square_container">
					<c:forEach var="user" items="${users}">
						<li class="person_square">
							<div>
								<img src="${user.avatarSrcSmall}">
							</div>
							<div>${user.name}</div>
							<div>${user.email}</div>
							<div>
								<c:choose>
									<c:when test="${user.isInMyNetwork}">
										${user.name} fait déjà parti de vos amis.
									</c:when>
									<c:when test="${not empty user.freeComment}">
										${user.freeComment}
									</c:when>
									<c:otherwise>
										<form method="POST" action="protected/demande_rejoindre_reseau">
											<input hidden="true" type="hidden" name="user_id" value="${user.id}" >
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
