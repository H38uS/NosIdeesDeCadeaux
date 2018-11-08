<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
	<jsp:body>

		<c:if test="${not empty idee}">
			Votre idée a bien été créée. Cliquez <a href="protected/ma_liste">ici</a> pour en ajouter d'autres !
			L'idée ajoutée:
			<t:template_une_idee />
		</c:if>

		<div id="mes_listes_list_users">
			<c:forEach var="user" items="${entities}">
				<a href="${identic_call_back}#list_${user.id}">${user.name}</a>
				<c:if test="${is_mobile}"> | </c:if>
			</c:forEach>
		</div>

		<div id="mes_listes_entities_container">
			
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
					<div class="row align-items-center pb-2">
						<div class="col-auto">
							Vous ne trouvez pas votre bonheur ? Recherchez une liste particulière : 
						</div>
						<form id="afficherliste_topmeslistes" class="form-inline" method="POST" action="protected/afficher_listes">
							<input type="text" class="form-control" name="name" id="top_mes_listes_search" placeholder="Entrez un nom ou un email" />
							<button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
						</form>
					</div>
				</c:if>
			</c:if>
		
			<c:if test="${not empty pages}">
				<div class="my-3">
					<ul class="pagination justify-content-center">
						<c:choose>
							<c:when test="${current != 1}">
								<li class="page-item">
									<a class="page-link" href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
								</li>
							</c:when>
							<c:otherwise>
								<li class="page-item disabled">
									<a class="page-link" href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
								</li>
							</c:otherwise>
						</c:choose>
						<c:forEach var="page" items="${pages}">
							<c:choose>
								<c:when test="${current != page.numero}">
									<li class="page-item">
										<a class="page-link" href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
									</li>
								</c:when>
								<c:otherwise>
									<li class="page-item active">
										<a class="page-link" href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
									</li>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:choose>
							<c:when test="${current != last}">
								<li class="page-item">
									<a class="page-link" href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
								</li>
							</c:when>
							<c:otherwise>
								<li class="page-item disabled">
									<a class="page-link" href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
								</li>
							</c:otherwise>
						</c:choose>
					</ul>
				</div>
			</c:if>
	
			<c:if test="${not empty entities}">
				
				<c:forEach var="user" items="${entities}">
					<c:if test="${userid == user.id}">
	
						<!-- Début idée de la personne -->
	
						<h2 id="list_${user.id}">Mes idées de cadeaux</h2>
						<c:if test="${fn:length(user.ideas) > 0}">
							<ul class="ideas_square_container">
								<c:forEach var="idea_from_liste" items="${user.ideas}">
									<c:if test="${empty idea_from_liste.surpriseBy}">
										<li class="idea_square top_tooltip">
										<div>
											<div class="modal fade" id="actions-idea-${idea_from_liste.id}" tabindex="-1" role="dialog" aria-hidden="true">
												<div class="modal-dialog modal-dialog-centered" role="document">
													<div class="modal-content">
														<div class="modal-header">
															<h5 class="modal-title" id="exampleModalLongTitle">Choisissez une action</h5>
															<button type="button" class="close" data-dismiss="modal" aria-label="Close">
																<span aria-hidden="true">&times;</span>
															</button>
														</div>
														<div class="modal-body">
															<div class="row align-items-center">
																<div class="col-3">
																	<a href="protected/modifier_idee?id=${idea_from_liste.id}&from=/${identic_call_back}" class="img">
																		<img src="resources/image/modifier.png"
																			 title="Modifier cette idée"
																			 width="${action_img_width}px" />
																	</a>
																</div>
																<div class="col-9 pl-0 text-left">
																	Modifier cette idée
																</div>
															</div>
															<div class="row align-items-center">
																<div class="col-3">
																	<a href="protected/remove_an_idea?ideeId=${idea_from_liste.id}&from=/${identic_call_back}" class="img idea_remove">
																		<img src="resources/image/supprimer.png"
																			 title="Supprimer cette idée"
																			 width="${action_img_width}px" />
																	</a>
																</div>
																<div class="col-9 pl-0 text-left">
																	Supprimer cette idée
																</div>
															</div>
															<div class="row align-items-center">
																<div class="col-3">
																	<a href="protected/idee_questions?idee=${idea_from_liste.id}" class="img">
																		<img src="resources/image/questions.png" title="Voir les questions existantes" width="${action_img_width}px" />
																	</a>
																</div>
																<div class="col-9 pl-0 text-left">
																	Voir les questions / Ajouter des précisions
																</div>
															</div>
															<div class="row align-items-center">
																<div class="col-3">
																	<a href="protected/je_le_veux_encore?idee=${idea_from_liste.id}&from=/${identic_call_back}" class="img">
																		<img src="resources/image/encore.png" title="J'ai déjà reçu cette idée, mais je la veux à nouveau ou je veux la suite." height="${action_img_width}px" />
																	</a>
																</div>
																<div class="col-9 pl-0 text-left">
																	Annuler toutes les réservations
																</div>
															</div>
															
														</div>
														<div class="modal-footer">
															<button type="button" class="btn btn-primary" data-dismiss="modal">Fermer</button>
														</div>
													</div>
												</div>
											</div>
											<div class="row justify-content-start align-items-center">
												<div class="col-auto pr-0">${idea_from_liste.priorite.image}</div>
												<c:if test="${not empty idea_from_liste.category}">
												<div class="col-auto px-0">
													<img src="resources/image/type/${idea_from_liste.category.image}" title="${idea_from_liste.category.title}" alt="${idea_from_liste.category.alt}" width="${action_img_width}px" />
												</div>
												</c:if>
												<c:if test="${idea_from_liste.hasQuestion()}">
												<div class="col-auto px-0">
													<a href="protected/idee_questions?idee=${idea_from_liste.id}" class="img">
														<img src="resources/image/questions.png" title="Il existe des questions/réponses sur cette idée" width="${action_img_width}px" />
													</a>
												</div>
												</c:if>
												<c:if test="${is_mobile}">
												<div class="col-auto ml-auto" data-toggle="modal" data-target="#actions-idea-${idea_from_liste.id}">
													<button class="btn btn-primary" >Actions...</button>
												</div>
												</c:if>
												<span class="outer_top_tooltiptext">
													<span class="top_tooltiptext">
														<a href="protected/modifier_idee?id=${idea_from_liste.id}&from=/${identic_call_back}" class="img">
															<img src="resources/image/modifier.png"
																 title="Modifier cette idée"
																 width="${action_img_width}px" />
														</a>
														<a href="protected/remove_an_idea?ideeId=${idea_from_liste.id}&from=/${identic_call_back}" class="img idea_remove">
															<img src="resources/image/supprimer.png"
																 title="Supprimer cette idée"
																 width="${action_img_width}px" />
														</a>
														<a href="protected/idee_questions?idee=${idea_from_liste.id}" class="img">
															<img src="resources/image/questions.png" title="Voir les questions existantes" width="${action_img_width}px" />
														</a>
														<a href="protected/je_le_veux_encore?idee=${idea_from_liste.id}&from=/${identic_call_back}" class="img">
															<img src="resources/image/encore.png" title="J'ai déjà reçu cette idée, mais je la veux à nouveau ou je veux la suite." height="${action_img_width}px" />
														</a>
													</span>
												</span>
											</div>
											<div class="left idea_square_text">
												${idea_from_liste.html}
											</div>
											<c:if test="${not empty idea_from_liste.image}">
												<div>
													<a href="${ideas_pictures}/${idea_from_liste.imageSrcLarge}" class="thickbox img" >
														<img src="${ideas_pictures}/${idea_from_liste.imageSrcSmall}" width="150" />
													</a>
												</div>
											</c:if>
											<div class="idea_square_modif_date" >
												Mise à jour le ${idea_from_liste.modificationDate}.
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
								<c:forEach var="idea_from_liste" items="${user.ideas}">
								<li class="idea_square top_tooltip ${idea_from_liste.displayClass}">
								<div>
									<div class="modal fade" id="actions-idea-${idea_from_liste.id}" tabindex="-1" role="dialog" aria-hidden="true">
										<div class="modal-dialog modal-dialog-centered" role="document">
											<div class="modal-content">
												<div class="modal-header">
													<h5 class="modal-title" id="exampleModalLongTitle">Choisissez une action</h5>
													<button type="button" class="close" data-dismiss="modal" aria-label="Close">
														<span aria-hidden="true">&times;</span>
													</button>
												</div>
												<div class="modal-body">
													<c:if test="${empty idea_from_liste.surpriseBy && not idea_from_liste.isBooked() && not idea_from_liste.isPartiallyBooked()}">
														<div class="row align-items-center">
															<div class="col-3 pr-0">
																<a href="protected/reserver?idee=${idea_from_liste.id}&from=/${identic_call_back}" class="img idea_reserver">
																	<img src="resources/image/reserver.png" class="clickable" title="Réserver l'idée" width="${action_img_width}px" />
																</a>
															</div>
															<div class="col-9 pl-0 text-left">
																Réserver l'idée
															</div>
														</div>
														<div class="row align-items-center">
															<div class="col-3 pr-0">
																<a href="protected/sous_reserver?idee=${idea_from_liste.id}" class="img">
																	<img src="resources/image/sous_partie.png" class="clickable" title="Réserver une sous-partie de l'idée" width="${action_img_width}px" />
																</a>
															</div>
															<div class="col-9 pl-0 text-left">
																Réserver une sous-partie de l'idée
															</div>
														</div>
														<div class="row align-items-center">
															<div class="col-3 pr-0">
																<a href="protected/create_a_group?idee=${idea_from_liste.id}" class="img">
																	<img src="resources/image/grouper.png" class="clickable" title="Créer un groupe" width="${action_img_width}px" />
																</a>
															</div>
															<div class="col-9 pl-0 text-left">
																Créer un groupe
															</div>
														</div>
													</c:if>
													<c:if test="${empty idea_from_liste.surpriseBy}">
														<div class="row align-items-center">
															<div class="col-3 pr-0">
																<a href="protected/est_a_jour?idee=${idea_from_liste.id}&from=/${identic_call_back}" class="img idea_est_a_jour">
																	<img src="resources/image/a_jour.png" class="clickable" title="Demander si c'est à jour." width="${action_img_width}px" />
																</a>
															</div>
															<div class="col-9 pl-0 text-left">
																Demander si c'est à jour
															</div>
														</div>
														<div class="row align-items-center">
															<div class="col-3 pr-0">
																<a href="protected/idee_questions?idee=${idea_from_liste.id}" class="img">
																	<img src="resources/image/questions.png" class="clickable" title="Poser une question à ${user.name} / voir les existantes" width="${action_img_width}px" />
																</a>
															</div>
															<div class="col-9 pl-0 text-left">
																Poser une question à ${user.name} / voir les existantes
															</div>
														</div>
													</c:if>
													<div class="row align-items-center">
														<div class="col-3 pr-0">
															<a href="protected/idee_commentaires?idee=${idea_from_liste.id}" class="img">
																<img src="resources/image/commentaires.png" title="Ajouter un commentaire / voir les existants" width="${action_img_width}px" />
															</a>
														</div>
														<div class="col-9 pl-0 text-left">
															Ajouter un commentaire / voir les existants
														</div>
													</div>
												</div>
												<div class="modal-footer">
													<button type="button" class="btn btn-primary" data-dismiss="modal">Fermer</button>
												</div>
											</div>
										</div>
									</div>
									<div class="row justify-content-start align-items-center">
										<div class="col-auto pr-0">${idea_from_liste.priorite.image}</div>
										<c:if test="${not empty idea_from_liste.category}">
										<div class="col-auto px-0">
											<img src="resources/image/type/${idea_from_liste.category.image}" title="${idea_from_liste.category.title}" alt="${idea_from_liste.category.alt}" width="${action_img_width}px" />
										</div>
										</c:if>
										<c:choose>
											<c:when test="${not empty idea_from_liste.surpriseBy}">
											<div class="col-auto px-0">
												<img src="resources/image/surprise.png" title="Idée surprise" width="${action_img_width}px" />
											</div>
											</c:when>
											<c:when test="${idea_from_liste.isBooked()}">
												<c:choose>
													<c:when test="${not empty idea_from_liste.bookingOwner}">
														<c:choose>
															<c:when test="${userid == idea_from_liste.bookingOwner.id}">
															<div class="col-auto px-0">	
																<a href="protected/dereserver?idee=${idea_from_liste.id}&from=/${identic_call_back}" class="img idea_dereserver">
																	<img src="resources/image/reserve-moi.png" title="Une de vos généreuse réservation - Cliquer pour annuler" alt="Idée réservée par vous" width="${action_img_width}px" />
																</a>
															</div>
															</c:when>
															<c:otherwise>
															<div class="col-auto px-0">
																<img src="resources/image/reserve-autre.png" title="Une réservation d'une autre personne plus rapide..." alt="Idée réservée par une autre personne" width="${action_img_width}px" />
															</div>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
													<div class="col-auto px-0">
														<a href="protected/detail_du_groupe?groupid=${idea_from_liste.groupKDO}" class="img">
															<img src="resources/image/reserve-groupe.png" title="Une réservation de groupe !" alt="Idée réservée par un groupe" width="${action_img_width}px" />
														</a>
													</div>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:when test="${idea_from_liste.isPartiallyBooked()}">
											<div class="col-auto px-0">
												<a href="protected/detail_sous_reservation?idee=${idea_from_liste.id}" class="img">
													<img src="resources/image/non-reserve.png" title="Un sous-ensemble de cette idée est réservé. Voyez si vous pouvez compléter !" alt="Sous partie de l'idée réservée" width="${action_img_width}px" />
												</a>
											</div>
											</c:when>
											<c:otherwise>
											<div class="col-auto px-0">
												<img src="resources/image/non-reserve.png" title="Cette idée est libre... Faite plaisir en l'offrant !" alt="Idée non réservée" width="${action_img_width}px" />
											</div>
											</c:otherwise>
										</c:choose>
										<c:if test="${idea_from_liste.hasAskedIfUpToDate()}">
										<div class="col-auto px-0">
											<img src="resources/image/a_jour.png" title="Vous avez envoyé une demande pour savoir si c'est à jour" alt="Demande est-ce à jour envoyée" width="${action_img_width}px" />
										</div>
										</c:if>
										<c:if test="${idea_from_liste.hasComment()}">
										<div class="col-auto px-0">
											<a href="protected/idee_commentaires?idee=${idea_from_liste.id}" class="img">
												<img src="resources/image/commentaires.png" title="Il existe des commentaires sur cette idée" width="${action_img_width}px" />
											</a>
										</div>
										</c:if>
										<c:if test="${idea_from_liste.hasQuestion()}">
										<div class="col-auto px-0">
											<a href="protected/idee_questions?idee=${idea_from_liste.id}" class="img">
												<img src="resources/image/questions.png" title="Il existe des questions/réponses sur cette idée" width="${action_img_width}px" />
											</a>
										</div>
										</c:if>
										<c:if test="${is_mobile}">
										<div class="col-auto ml-auto" data-toggle="modal" data-target="#actions-idea-${idea_from_liste.id}">
											<button class="btn btn-primary" >Actions...</button>
										</div>
										</c:if>
										<span class="outer_top_tooltiptext">
											<span class="top_tooltiptext">
												<c:if test="${empty idea_from_liste.surpriseBy && not idea_from_liste.isBooked() && not idea_from_liste.isPartiallyBooked()}">
													<a href="protected/reserver?idee=${idea_from_liste.id}&from=/${identic_call_back}" class="img idea_reserver">
														<img src="resources/image/reserver.png" class="clickable" title="Réserver l'idée" width="${action_img_width}px" />
													</a>
													<a href="protected/sous_reserver?idee=${idea_from_liste.id}" class="img">
														<img src="resources/image/sous_partie.png" class="clickable" title="Réserver une sous-partie de l'idée" width="${action_img_width}px" />
													</a>
													<a href="protected/create_a_group?idee=${idea_from_liste.id}" class="img">
														<img src="resources/image/grouper.png" class="clickable" title="Créer un groupe" width="${action_img_width}px" />
													</a>
												</c:if>
												<c:if test="${empty idea_from_liste.surpriseBy}">
													<a href="protected/est_a_jour?idee=${idea_from_liste.id}&from=/${identic_call_back}" class="img idea_est_a_jour">
														<img src="resources/image/a_jour.png" class="clickable" title="Demander si c'est à jour." width="${action_img_width}px" />
													</a>
													<a href="protected/idee_questions?idee=${idea_from_liste.id}" class="img">
														<img src="resources/image/questions.png" class="clickable" title="Poser une question à ${user.name} / voir les existantes" width="${action_img_width}px" />
													</a>
												</c:if>
												<a href="protected/idee_commentaires?idee=${idea_from_liste.id}" class="img">
													<img src="resources/image/commentaires.png" title="Ajouter un commentaire / voir les existants" width="${action_img_width}px" />
												</a>
											</span>
										</span>
									</div>
									<div class="left idea_square_text">
										${idea_from_liste.html}
									</div>
									<c:if test="${not empty idea_from_liste.image}">
										<div>
											<a href="${ideas_pictures}/${idea_from_liste.imageSrcLarge}" class="thickbox img" >
												<img src="${ideas_pictures}/${idea_from_liste.imageSrcSmall}" width="150" />
											</a>
										</div>
									</c:if>
									<div class="idea_square_modif_date" >
										<c:choose>
												<c:when test="${not empty idea_from_liste.surpriseBy}">
													<c:choose>
														<c:when test="${idea_from_liste.surpriseBy.id == userid}">
															Idée surprise créée le ${idea_from_liste.modificationDate} par vous - l'<a href="protected/supprimer_surprise?idee=${idea_from_liste.id}&from=/${identic_call_back}">annuler</a>.
														</c:when>
														<c:otherwise>
															Idée surprise créée le ${idea_from_liste.modificationDate} par ${idea_from_liste.surpriseBy.name}.
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:when test="${idea_from_liste.isBooked()}">
													<c:choose>
														<c:when test="${not empty idea_from_liste.bookingOwner}">
															<c:choose>
																<c:when test="${userid == idea_from_liste.bookingOwner.id}">
																	Réservée par vous le ${idea_from_liste.bookingDate} - <a href="protected/dereserver?idee=${idea_from_liste.id}&from=/${identic_call_back}" class="idea_dereserver">Annuler</a> !
																</c:when>
																<c:otherwise>
																	Réservée par ${idea_from_liste.bookingOwner.name} le ${idea_from_liste.bookingDate}
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															Réservée par un groupe (créé le ${idea_from_liste.bookingDate}).
															<a href="protected/detail_du_groupe?groupid=${idea_from_liste.groupKDO}">Voir le détail du groupe</a>.
														</c:otherwise>
													</c:choose><br/>
												</c:when>
												<c:when test="${idea_from_liste.isPartiallyBooked()}">
													Une sous partie de l'idée est actuellement réservée.
													<a href="protected/detail_sous_reservation?idee=${idea_from_liste.id}">Voir le détail.</a><br/>
												</c:when>
												<c:otherwise>
														Non réservée, modifiée le ${idea_from_liste.modificationDate}.
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
					<div class="my-3">
						<ul class="pagination justify-content-center">
							<c:choose>
								<c:when test="${current != 1}">
									<li class="page-item">
										<a class="page-link" href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
									</li>
								</c:when>
								<c:otherwise>
									<li class="page-item disabled">
										<a class="page-link" href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
									</li>
								</c:otherwise>
							</c:choose>
							<c:forEach var="page" items="${pages}">
								<c:choose>
									<c:when test="${current != page.numero}">
										<li class="page-item">
											<a class="page-link" href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
										</li>
									</c:when>
									<c:otherwise>
										<li class="page-item active">
											<a class="page-link" href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
										</li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							<c:choose>
								<c:when test="${current != last}">
									<li class="page-item">
										<a class="page-link" href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
									</li>
								</c:when>
								<c:otherwise>
									<li class="page-item disabled">
										<a class="page-link" href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
									</li>
								</c:otherwise>
							</c:choose>
						</ul>
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
				<div class="row align-items-center pb-5">
				<c:choose>
					<c:when test="${fn:length(entities) eq 1}">
						<div class="col-auto">
							Consultez une autre liste : 
						</div>
						<form id="afficherliste_bottommeslistes" class="form-inline"  method="POST" action="protected/afficher_listes">
							<input type="text" class="form-control" name="name" id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email" />
							<button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
						</form>
					</c:when>
					<c:otherwise>
						<div class="col-auto">
							Vous ne trouvez pas votre bonheur ? Recherchez une liste particulière : 
						</div>
						<form id="afficherliste_bottommeslistes" class="form-inline" method="POST" action="protected/afficher_listes">
							<input type="text" class="form-control" name="name" id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email" />
							<button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
						</form>
					</c:otherwise>
				</c:choose>
				</div>
			</c:if>
		</div>
	</jsp:body>
</t:normal_protected>