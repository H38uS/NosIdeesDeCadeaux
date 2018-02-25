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
		<c:forEach var="user" items="${entities}">
			<a href="${identic_call_back}#list_${user.id}">${user.name}</a> |
		</c:forEach>
		<c:if test="${fn:length(entities) gt 1}">
			<script type="text/javascript">
				$(document).ready(function() {
					$("#top_mes_listes_search").autocomplete({
						source : "protected/service/name_resolver",
						minLength : 2,
						select : function(event, ui) {
							$("#top_mes_listes_search").val(ui.item.email);
							$("#afficherliste_topmeslistes").submit();
							return false;
						}
					});
				});
			</script>
			<div class="meslistes_search">
				Vous ne trouvez pas votre bonheur ? Recherchez une liste particulière : 
				<form id="afficherliste_topmeslistes" method="POST" action="protected/afficher_listes">
					<input type="text" name="name" id="top_mes_listes_search" placeholder="Entrez un nom ou un email" />
					<input type="submit" value="Rechercher !" />
				</form>
			</div>
		</c:if>
		<c:if test="${not empty entities}">
			<c:forEach var="user" items="${entities}">
				<c:if test="${userid == user.id}">
					<!-- Début idée de la personne -->
					<h2 id="list_${user.id}">Mes idées de cadeaux</h2>
					<c:if test="${fn:length(user.ideas) > 0}">
						<ul id="ideas_square_container">
							<c:forEach var="idee" items="${user.ideas}">
								<c:if test="${empty idee.surpriseBy}">
									<li class="idea_square top_tooltip">
										<div class="left">
											<span>${idee.priorite.image}</span>
											<c:if test="${not empty idee.category}">
												<img src="public/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" />
											</c:if>
											<c:if test="${idee.hasQuestion()}">
												<a href="protected/idee_questions?idee=${idee.id}">
													<img src="public/image/questions.png" title="Il existe des questions/réponses sur cette idée" />
												</a>
											</c:if>
											<span class="outer_top_tooltiptext">
												<span class="top_tooltiptext">
													<a href="protected/modifier_idee?id=${idee.id}">Modifier</a>
													ou 
													<a href="protected/remove_an_idea?ideeId=${idee.id}&from=/${identic_call_back}">supprimer</a>
													cette idée.<br/>
													<a href="protected/idee_questions?idee=${idee.id}">
														<img src="public/image/questions.png" title="Voir les questions existantes" />
													</a>
												</span>
											</span>
										</div>
										<div class="left">
											${idee.html}
										</div>
										<c:if test="${not empty idee.image}">
											<div>
												<a href="${ideas_pictures}/${idee.imageSrcLarge}" class="thickbox img" >
													<img src="${ideas_pictures}/${idee.imageSrcSmall}" width="150" />
												</a>
											</div>
										</c:if>
									</li>
								</c:if>
							</c:forEach>
						</ul>
					</c:if>
					<c:if test="${fn:length(user.ideas) == 0}">
						<span>Vous n'avez pas encore d'idées. Cliquez <a href="protected/ma_liste">ici</a> pour en ajouter.</span>
					</c:if>
				</c:if>
				<!-- Fin idée de la personne -->
	
				<c:if test="${userid != user.id}">
					<h2 id="list_${user.id}">Liste de cadeaux ${user.myDName}</h2>
					<div>
						<ul id="ideas_square_container">
							<c:forEach var="idee" items="${user.ideas}">
							<li class="idea_square top_tooltip ${idee.displayClass}">
								<div class="left">
									<span>${idee.priorite.image}</span>
									<c:if test="${not empty idee.category}">
										<img src="public/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" />
									</c:if>
									<c:choose>
										<c:when test="${not empty idee.surpriseBy}">
											<img src="public/image/surprise.png" title="Idée surprise" />
										</c:when>
										<c:when test="${idee.isBooked()}">
											<c:choose>
												<c:when test="${not empty idee.bookingOwner}">
													<c:choose>
														<c:when test="${userid == idee.bookingOwner.id}">
															<img src="public/image/reserve-moi.png" title="Une de vos généreuse réservation" alt="Idée réservée par vous" />
														</c:when>
														<c:otherwise>
															<img src="public/image/reserve-autre.png" title="Une réservation d'une autre personne plus rapide..." alt="Idée réservée par une autre personne" />
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<img src="public/image/reserve-groupe.png" title="Une réservation de groupe !" alt="Idée réservée par un groupe" />
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<img src="public/image/non-reserve.png" title="Cette idée est libre... Faite plaisir en l'offrant !" alt="Idée non réservée" />
										</c:otherwise>
									</c:choose>
									<c:if test="${idee.hasComment()}">
										<a href="protected/idee_commentaires?idee=${idee.id}">
											<img src="public/image/commentaires.png" title="Il existe des commentaires sur cette idée" />
										</a>
									</c:if>
									<c:if test="${idee.hasQuestion()}">
										<a href="protected/idee_questions?idee=${idee.id}">
											<img src="public/image/questions.png" title="Il existe des questions/réponses sur cette idée" />
										</a>
									</c:if>
									<span class="outer_top_tooltiptext">
										<span class="top_tooltiptext">
											<c:choose>
												<c:when test="${not empty idee.surpriseBy}">
													<c:choose>
														<c:when test="${idee.surpriseBy.id == userid}">
															Idée surprise créée par vous - l'<a href="protected/supprimer_surprise?idee=${idee.id}&from=/${identic_call_back}">annuler</a>.
														</c:when>
														<c:otherwise>
															Idée surprise créée par ${idee.surpriseBy.name}.
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:when test="${idee.isBooked()}">
													<c:choose>
														<c:when test="${not empty idee.bookingOwner}">
															<c:choose>
																<c:when test="${userid == idee.bookingOwner.id}">
																	Réservée par vous le ${idee.bookingDate} - <a href="protected/dereserver?idee=${idee.id}&from=/${identic_call_back}">Annuler</a> !
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
													</c:choose><br/>
												</c:when>
												<c:when test="${idee.isPartiallyBooked()}">
													Une sous partie de l'idée est actuellement réservée.
													<a href="protected/detail_sous_reservation?idee=${idee.id}">Voir le détail.</a><br/>
												</c:when>
												<c:otherwise>
														L'idée n'a pas encore été réservée.<br/>
														<a href="protected/reserver?idee=${idee.id}&from=/${identic_call_back}" class="img">
															<img src="public/image/reserver.png" class="clickable" title="Réserver l'idée" />
														</a>
														<a href="protected/sous_reserver?idee=${idee.id}" class="img">
															<img src="public/image/sous_partie.png" class="clickable" title="Réserver une sous-partie de l'idée" />
														</a>
														<a href="protected/create_a_group?idee=${idee.id}" class="img">
															<img src="public/image/grouper.png" class="clickable" title="Créer un groupe" />
														</a>
												</c:otherwise>
											</c:choose>
											<c:if test="${empty idee.surpriseBy}">
												<a href="protected/est_a_jour?idee=${idee.id}&from=/${identic_call_back}" class="img">
													<img src="public/image/a_jour.png" class="clickable" title="Demander si c'est à jour." />
												</a>
												<a href="protected/idee_questions?idee=${idee.id}" class="img">
													<img src="public/image/questions.png" class="clickable" title="Poser une question à ${user.name} / voir les existantes" />
												</a>
											</c:if>
											<a href="protected/idee_commentaires?idee=${idee.id}" class="img">
												<img src="public/image/commentaires.png" title="Ajouter un commentaire / voir les existants" />
											</a>
										</span>
									</span>
								</div>
								<div class="left">
									${idee.html}
								</div>
								<c:if test="${not empty idee.image}">
									<div>
										<a href="${ideas_pictures}/${idee.imageSrcLarge}" class="thickbox img" >
											<img src="${ideas_pictures}/${idee.imageSrcSmall}" width="150" />
										</a>
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
		</c:if>
		<c:if test="${empty entities}">
			<div>
				<p>Aucune liste trouvée...</p>
				<p>
				 Vous pouvez entrer un nouveau nom ci-dessous, ou cliquer sur <a href="protected/mes_listes">ce lien</a> 
				 pour afficher toutes vos listes.
				</p>
			</div>
		</c:if>
		<script type="text/javascript">
			$(document).ready(function() {
				$("#bottom_mes_listes_search").autocomplete({
					source : "protected/service/name_resolver",
					minLength : 2,
					select : function(event, ui) {
						$("#bottom_mes_listes_search").val(ui.item.email);
						$("#afficherliste_bottommeslistes").submit();
						return false;
					}
				});
			});
		</script>
		<c:choose>
			<c:when test="${fn:length(entities) eq 1}">
				<div class="meslistes_search">
					Consultez une autre liste : 
					<form id="afficherliste_bottommeslistes" method="POST" action="protected/afficher_listes">
						<input type="text" name="name" id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email" />
						<input type="submit" value="Rechercher !" />
					</form>
					<br/><br/><br/><br/><br/><br/><br/><br/><br/>
				</div>
			</c:when>
			<c:otherwise>
				<div class="meslistes_search">
					Vous ne trouvez pas votre bonheur ? Recherchez une liste particulière : 
					<form id="afficherliste_bottommeslistes" method="POST" action="protected/afficher_listes">
						<input type="text" name="name" id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email" />
						<input type="submit" value="Rechercher !" />
					</form>
					<br/><br/><br/><br/><br/><br/><br/><br/><br/>
				</div>
			</c:otherwise>
		</c:choose>
	</jsp:body>
</t:normal_protected>