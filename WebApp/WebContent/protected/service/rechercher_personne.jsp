<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

			<c:if test="${not empty entities}">
				<c:if test="${not empty pages}">
					<div>
						<ul class="pagination justify-content-center">
							<c:choose>
								<c:when test="${current != 1}">
									<li class="page-item">
										<a class="page-link" href="protected/rechercher_personne?page=${current-1}&only_non_friend=${onlyNonFriend}&name=${name}">Précédent</a>
									</li>
								</c:when>
								<c:otherwise>
									<li class="page-item disabled">
										<a class="page-link" href="protected/rechercher_personne?page=${current-1}&only_non_friend=${onlyNonFriend}&name=${name}">Précédent</a>
									</li>
								</c:otherwise>
							</c:choose>
							<c:forEach var="page" items="${pages}">
								<c:choose>
									<c:when test="${current != page.numero}">
										<li class="page-item">
											<a class="page-link" href="protected/rechercher_personne?page=${page.numero}&only_non_friend=${onlyNonFriend}&name=${name}">${page.numero}</a>
										</li>
									</c:when>
									<c:otherwise>
										<li class="page-item active">
											<a class="page-link" href="protected/rechercher_personne?page=${page.numero}&only_non_friend=${onlyNonFriend}&name=${name}">${page.numero}</a>
										</li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							<c:choose>
								<c:when test="${current != last}">
									<li class="page-item">
										<a class="page-link" href="protected/rechercher_personne?page=${current+1}&only_non_friend=${onlyNonFriend}&name=${name}">Suivant</a>
									</li>
								</c:when>
								<c:otherwise>
									<li class="page-item disabled">
										<a class="page-link" href="protected/rechercher_personne?page=${current+1}&only_non_friend=${onlyNonFriend}&name=${name}">Suivant</a>
									</li>
								</c:otherwise>
							</c:choose>
						</ul>
					</div>
				</c:if>
				<div class="row align-items-start mx-0 justify-content-around">
					<c:forEach var="user" items="${entities}">
						<div class="card col-auto px-0 m-2" style="width:250px">
							<img class="card-img-top" src="${avatars}/${user.avatarSrcSmall}">
							<div class="card-body">
								<h5 class="card-title">
									<div>${user.name}</div>
									<div>${user.email}</div>
								</h5>
							</div>
							<div class="card-footer text-center">
								<c:choose>
									<c:when test="${user.isInMyNetwork}">
										<span class="verticalcenter_helper"></span>
										<img class="verticalcenter" alt="${user.name} fait déjà parti de vos amis." title="${user.name} fait déjà parti de vos amis." src="resources/image/friend.png">
									</c:when>
									<c:when test="${not empty user.freeComment}">
										<span class="verticalcenter_helper"></span>
										<img class="verticalcenter" alt="${user.freeComment}" title="${user.freeComment}" src="resources/image/demande_envoyee.jpg">
									</c:when>
									<c:otherwise>
										<form method="POST" action="protected/demande_rejoindre_reseau">
											<input type="hidden" name="user_id" value="${user.id}" >
											<button class="envoyer_demande_reseau btn btn-primary" type="submit" name="submit" id="submit">Envoyer une demande</button>
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										</form>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</c:forEach>
				</div>
			</c:if>
			<c:if test="${not empty pages}">
				<div>
					<ul class="pagination justify-content-center">
						<c:choose>
							<c:when test="${current != 1}">
								<li class="page-item">
									<a class="page-link" href="protected/rechercher_personne?page=${current-1}&only_non_friend=${onlyNonFriend}&name=${name}">Précédent</a>
								</li>
							</c:when>
							<c:otherwise>
								<li class="page-item disabled">
									<a class="page-link" href="protected/rechercher_personne?page=${current-1}&only_non_friend=${onlyNonFriend}&name=${name}">Précédent</a>
								</li>
							</c:otherwise>
						</c:choose>
						<c:forEach var="page" items="${pages}">
							<c:choose>
								<c:when test="${current != page.numero}">
									<li class="page-item">
										<a class="page-link" href="protected/rechercher_personne?page=${page.numero}&only_non_friend=${onlyNonFriend}&name=${name}">${page.numero}</a>
									</li>
								</c:when>
								<c:otherwise>
									<li class="page-item active">
										<a class="page-link" href="protected/rechercher_personne?page=${page.numero}&only_non_friend=${onlyNonFriend}&name=${name}">${page.numero}</a>
									</li>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:choose>
							<c:when test="${current != last}">
								<li class="page-item">
									<a class="page-link" href="protected/rechercher_personne?page=${current+1}&only_non_friend=${onlyNonFriend}&name=${name}">Suivant</a>
								</li>
							</c:when>
							<c:otherwise>
								<li class="page-item disabled">
									<a class="page-link" href="protected/rechercher_personne?page=${current+1}&only_non_friend=${onlyNonFriend}&name=${name}">Suivant</a>
								</li>
							</c:otherwise>
						</c:choose>
					</ul>
				</div>
			</c:if>