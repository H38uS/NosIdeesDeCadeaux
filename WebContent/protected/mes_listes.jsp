<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
	<jsp:body>

		<div id="mes_listes_list_users">
			<c:forEach var="user" items="${entities}">
				<a href="${identic_call_back}#list_${user.id}">${user.name}</a>
				<c:if test="${is_mobile}"> | </c:if>
			</c:forEach>
		</div>

		<div id="mes_listes_entities_container">

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
	
			<c:if test="${not is_mobile}">
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
			</c:if>
	
			<c:if test="${not empty entities}">
				
				<c:forEach var="user" items="${entities}">
					<c:if test="${userid == user.id}">
	
						<!-- Début idée de la personne -->
	
						<h2 id="list_${user.id}">Mes idées de cadeaux</h2>
						<c:if test="${fn:length(user.ideas) > 0}">
							<ul class="ideas_square_container">
								<c:forEach var="idee" items="${user.ideas}">
									<c:if test="${empty idee.surpriseBy}">
										<li class="idea_square top_tooltip">
										<div>
											<div class="left">
												<span>${idee.priorite.image}</span>
												<c:if test="${not empty idee.category}">
													<img src="resources/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" width="${action_img_width}px" />
												</c:if>
												<c:if test="${idee.hasQuestion()}">
													<a href="protected/idee_questions?idee=${idee.id}">
														<img src="resources/image/questions.png" title="Il existe des questions/réponses sur cette idée" width="${action_img_width}px" />
													</a>
												</c:if>
												<span class="outer_top_tooltiptext">
													<span class="top_tooltiptext">
														<a href="protected/modifier_idee?id=${idee.id}&from=/${identic_call_back}">
															<img src="resources/image/modifier.png"
																 title="Modifier cette idée"
																 width="${action_img_width}px" />
														</a>
														<a href="protected/remove_an_idea?ideeId=${idee.id}&from=/${identic_call_back}">
															<img src="resources/image/supprimer.png"
																 title="Supprimer cette idée"
																 width="${action_img_width}px" />
														</a>
														<a href="protected/idee_questions?idee=${idee.id}">
															<img src="resources/image/questions.png" title="Voir les questions existantes" width="${action_img_width}px" />
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
											<div class="idea_square_modif_date" >
												Mise à jour le ${idee.modificationDate}.
											</div>
										</div>
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
						<c:if test="${fn:length(user.ideas) > 0}">
							<ul class="ideas_square_container">
								<c:forEach var="idee" items="${user.ideas}">
								<li class="idea_square top_tooltip ${idee.displayClass}">
								<div>
									<div class="left">
										<span>${idee.priorite.image}</span>
										<c:if test="${not empty idee.category}">
											<img src="resources/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" width="${action_img_width}px" />
										</c:if>
										<c:choose>
											<c:when test="${not empty idee.surpriseBy}">
												<img src="resources/image/surprise.png" title="Idée surprise" width="${action_img_width}px" />
											</c:when>
											<c:when test="${idee.isBooked()}">
												<c:choose>
													<c:when test="${not empty idee.bookingOwner}">
														<c:choose>
															<c:when test="${userid == idee.bookingOwner.id}">
																<a href="protected/dereserver?idee=${idee.id}&from=/${identic_call_back}">
																	<img src="resources/image/reserve-moi.png" title="Une de vos généreuse réservation - Cliquer pour annuler" alt="Idée réservée par vous" width="${action_img_width}px" />
																</a>
															</c:when>
															<c:otherwise>
																<img src="resources/image/reserve-autre.png" title="Une réservation d'une autre personne plus rapide..." alt="Idée réservée par une autre personne" width="${action_img_width}px" />
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<a href="protected/detail_du_groupe?groupid=${idee.groupKDO}">
															<img src="resources/image/reserve-groupe.png" title="Une réservation de groupe !" alt="Idée réservée par un groupe" width="${action_img_width}px" />
														</a>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:when test="${idee.isPartiallyBooked()}">
												<a href="protected/detail_sous_reservation?idee=${idee.id}">
													<img src="resources/image/non-reserve.png" title="Un sous-ensemble de cette idée est réservé. Voyez si vous pouvez compléter !" alt="Sous partie de l'idée réservée" width="${action_img_width}px" />
												</a>
											</c:when>
											<c:otherwise>
												<img src="resources/image/non-reserve.png" title="Cette idée est libre... Faite plaisir en l'offrant !" alt="Idée non réservée" width="${action_img_width}px" />
											</c:otherwise>
										</c:choose>
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
												<c:if test="${empty idee.surpriseBy && not idee.isBooked() && not idee.isPartiallyBooked()}">
													<a href="protected/reserver?idee=${idee.id}&from=/${identic_call_back}" class="img">
														<img src="resources/image/reserver.png" class="clickable" title="Réserver l'idée" width="${action_img_width}px" />
													</a>
													<a href="protected/sous_reserver?idee=${idee.id}" class="img">
														<img src="resources/image/sous_partie.png" class="clickable" title="Réserver une sous-partie de l'idée" width="${action_img_width}px" />
													</a>
													<a href="protected/create_a_group?idee=${idee.id}" class="img">
														<img src="resources/image/grouper.png" class="clickable" title="Créer un groupe" width="${action_img_width}px" />
													</a>
												</c:if>
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
									<div class="idea_square_modif_date" >
										<c:choose>
												<c:when test="${not empty idee.surpriseBy}">
													<c:choose>
														<c:when test="${idee.surpriseBy.id == userid}">
															Idée surprise créée le ${idee.modificationDate} par vous - l'<a href="protected/supprimer_surprise?idee=${idee.id}&from=/${identic_call_back}">annuler</a>.
														</c:when>
														<c:otherwise>
															Idée surprise créée le ${idee.modificationDate} par ${idee.surpriseBy.name}.
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
															Réservée par un groupe (créé le ${idee.bookingDate}).
															<a href="protected/detail_du_groupe?groupid=${idee.groupKDO}">Voir le détail du groupe</a>.
														</c:otherwise>
													</c:choose><br/>
												</c:when>
												<c:when test="${idee.isPartiallyBooked()}">
													Une sous partie de l'idée est actuellement réservée.
													<a href="protected/detail_sous_reservation?idee=${idee.id}">Voir le détail.</a><br/>
												</c:when>
												<c:otherwise>
														Non réservée, modifiée le ${idee.modificationDate}.
												</c:otherwise>
											</c:choose>
									</div>
								</div>
								</li>
								</c:forEach>
							</ul>
						</c:if>
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
			<c:if test="${not is_mobile}">
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
			</c:if>
		</div>
	</jsp:body>
</t:normal_protected>