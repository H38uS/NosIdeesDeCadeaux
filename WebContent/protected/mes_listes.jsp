<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
	<jsp:body>
		<c:if test="${not empty pages}">
			<div class="center">
				<c:if test="${current != 1}">
					<a href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
				</c:if>
				<c:forEach var="page" items="${pages}">
					<c:choose>
						<c:when test="${current != page.numero}">
							<a href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
						</c:when>
						<c:otherwise>
							${page.numero}
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<c:if test="${current != last}">
					<a href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
				</c:if>
			</div>
		</c:if>
		<c:forEach var="user" items="${users}">
			<a href="${identic_call_back}#list_${user.id}">${user.name}</a> |
		</c:forEach>
		<c:forEach var="user" items="${users}">
			<c:if test="${userid == user.id}">
				<!-- Début idée de la personne -->
				<h2 id="list_${user.id}">Mes idées de cadeaux</h2>
				<c:if test="${fn:length(user.ideas) > 0}">
					<ul id="ideas_square_container">
						<c:forEach var="idee" items="${user.ideas}">
						<li class="idea_square top_tooltip">
							<div class="left">
								<c:if test="${not empty idee.category}">
									<img src="public/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" />
								</c:if>
								<c:if test="${idee.hasQuestion()}">
									<img src="public/image/questions.png" title="Il existe des questions/réponses sur cette idée" />
								</c:if>
								<span class="top_tooltiptext">
									<a href="protected/modifier_idee?id=${idee.id}">Modifier</a>
									ou 
									<a href="protected/remove_an_idea?ideeId=${idee.id}">supprimer</a>
									cette idée.<br/>
									<a href="protected/idee_questions?idee=${idee.id}">Voir les questions existantes</a>.
								</span>
							</div>
							${idee.html}
							<c:if test="${not empty idee.image}">
								<div>
									<img src="${ideas_pictures}/${idee.imageSrcSmall}" width="150" />
								</div>
							</c:if>
						</li>
						</c:forEach>
					</ul>
				</c:if>
				<c:if test="${fn:length(user.ideas) == 0}">
					<span>Vous n'avez pas encore d'idées. Cliquez <a href="protected/ma_liste">ici</a> pour en ajouter.</span>
				</c:if>
			</c:if>
			<!-- Fin idée de la personne -->

			<c:if test="${userid != user.id}">
				<h2 id="list_${user.id}">Liste de cadeaux de ${user.name}</h2>
				<div>
					<ul id="ideas_square_container">
						<c:forEach var="idee" items="${user.ideas}">
						<li class="idea_square top_tooltip ${idee.displayClass}">
							<div class="left">
								<c:if test="${not empty idee.category}">
									<img src="public/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" />
								</c:if>
								<c:choose>
									<c:when test="${idee.isBooked()}">
										<c:choose>
											<c:when test="${not empty idee.bookingOwner}">
												<c:choose>
													<c:when test="${userid == idee.bookingOwner.id}">
														<img src="public/image/reserve-moi.png" alt="Idée réservée par vous" />
													</c:when>
													<c:otherwise>
														<img src="public/image/reserve-autre.png" alt="Idée réservée par une autre personne" />
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<img src="public/image/reserve-groupe.png" alt="Idée réservée par un groupe" />
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<img src="public/image/non-reserve.png" alt="Idée non réservée" />
									</c:otherwise>
								</c:choose>
								<c:if test="${idee.hasComment()}">
									<img src="public/image/commentaires.png" title="Il existe des commentaires sur cette idée" />
								</c:if>
								<c:if test="${idee.hasQuestion()}">
									<img src="public/image/questions.png" title="Il existe des questions/réponses sur cette idée" />
								</c:if>
								<span class="top_tooltiptext">
									<c:choose>
										<c:when test="${idee.isBooked()}">
												<c:choose>
													<c:when test="${not empty idee.bookingOwner}">
														<c:choose>
															<c:when test="${userid == idee.bookingOwner.id}">
																Réservée par vous le ${idee.bookingDate} - <a href="protected/dereserver?&idee=${idee.id}">Annuler</a> !
															</c:when>
															<c:otherwise>
																Réservée par ${idee.bookingOwner.name} le ${idee.bookingDate}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														L'idée est réservée par un groupe (créé le ${idee.bookingDate}).
														<a href="protected/detail_du_groupe?groupid=${idee.groupKDO}">Voir le détail du groupe</a>.
													</c:otherwise>
												</c:choose>
										</c:when>
										<c:when test="${idee.isPartiallyBooked()}">
											Une sous partie de l'idée est actuellement réservée.
											<a href="protected/detail_sous_reservation?idee=${idee.id}">Voir le détail.</a>
										</c:when>
										<c:otherwise>
												L'idée n'a pas encore été réservée.<br/>
												<a href="protected/reserver?idee=${idee.id}">La réserver</a>,
												<a href="protected/sous_reserver?idee=${idee.id}"> réserver une sous-partie</a>
												ou <a href="protected/create_a_group?idee=${idee.id}">créer un groupe</a>
										</c:otherwise>
									</c:choose><br/>
									<a href="protected/est_a_jour?idee=${idee.id}">Demander</a> si c'est à jour.<br/>
									<a href="protected/idee_questions?idee=${idee.id}">Poser une question à ${user.name} / voir les existantes</a>.<br/>
									<a href="protected/idee_commentaires?idee=${idee.id}">Ajouter un commentaire / voir les existants</a>.
								</span>
							</div>
							${idee.html}
							<c:if test="${not empty idee.image}">
								<div>
									<img src="${ideas_pictures}/${idee.imageSrcSmall}" width="150" />
								</div>
							</c:if>
						</li>
						</c:forEach>
					</ul>
				</div>
				<c:if test="${fn:length(user.ideas) == 0}">
					<span>${user.name} n'a pas encore d'idées.</span>
				</c:if>
			</c:if>
		</c:forEach>
		<c:if test="${not empty pages}">
			<div class="center">
				<c:if test="${current != 1}">
					<a href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
				</c:if>
				<c:forEach var="page" items="${pages}">
					<c:choose>
						<c:when test="${current != page.numero}">
							<a href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
						</c:when>
						<c:otherwise>
							${page.numero}
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<c:if test="${current != last}">
					<a href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
				</c:if>
			</div>
		</c:if>
	</jsp:body>
</t:normal_protected>