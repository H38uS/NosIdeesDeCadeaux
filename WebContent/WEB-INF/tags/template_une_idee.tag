<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>

<c:choose>
	<c:when test="${userid == idee.owner.id}">
		<!-- Début idée de la personne -->
		<ul class="ideas_square_container">
			<li class="idea_square top_tooltip">
			<div>
				<div class="modal fade" id="actions-idea-${idee.id}" tabindex="-1" role="dialog" aria-hidden="true">
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
										<a href="protected/modifier_idee?id=${idee.id}&from=/${identic_call_back}" class="img">
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
										<a href="protected/remove_an_idea?ideeId=${idee.id}&from=/${identic_call_back}" class="img idea_remove">
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
										<a href="protected/idee_questions?idee=${idee.id}" class="img">
											<img src="resources/image/questions.png" title="Voir les questions existantes" width="${action_img_width}px" />
										</a>
									</div>
									<div class="col-9 pl-0 text-left">
										Voir les questions / Ajouter des précisions
									</div>
								</div>
								<div class="row align-items-center">
									<div class="col-3">
										<a href="protected/je_le_veux_encore?idee=${idee.id}&from=/${identic_call_back}" class="img">
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
				<div class="row justify-content-start">
					<div class="col-auto pr-0">${idee.priorite.image}</div>
					<c:if test="${not empty idee.category}">
					<div class="col-auto px-0">
						<img src="resources/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" width="${action_img_width}px" />
					</div>
					</c:if>
					<c:if test="${idee.hasQuestion()}">
					<div class="col-auto px-0">
						<a href="protected/idee_questions?idee=${idee.id}" class="img">
							<img src="resources/image/questions.png" title="Il existe des questions/réponses sur cette idée" width="${action_img_width}px" />
						</a>
					</div>
					</c:if>
					<c:if test="${is_mobile}">
					<div class="col-auto ml-auto">
						<button class="btn btn-primary mobile_actions" data-toggle="modal" data-target="#actions-idea-${idee.id}">Actions...</button>
					</div>
					</c:if>
					<span class="outer_top_tooltiptext">
						<span class="top_tooltiptext">
							<a href="protected/modifier_idee?id=${idee.id}&from=/${identic_call_back}" class="img">
								<img src="resources/image/modifier.png"
									 title="Modifier cette idée"
									 width="${action_img_width}px" />
							</a>
							<a href="protected/remove_an_idea?ideeId=${idee.id}&from=/${identic_call_back}" class="img idea_remove">
								<img src="resources/image/supprimer.png"
									 title="Supprimer cette idée"
									 width="${action_img_width}px" />
							</a>
							<a href="protected/idee_questions?idee=${idee.id}" class="img">
								<img src="resources/image/questions.png" title="Voir les questions existantes" width="${action_img_width}px" />
							</a>
							<a href="protected/je_le_veux_encore?idee=${idee.id}&from=/${identic_call_back}" class="img">
								<img src="resources/image/encore.png" title="J'ai déjà reçu cette idée, mais je la veux à nouveau ou je veux la suite." height="${action_img_width}px" />
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
		</ul>
		<!-- Fin idée de la personne -->
	</c:when>
	<c:otherwise>
		<ul class="ideas_square_container">
			<li class="idea_square top_tooltip ${idee.displayClass}">
			<div>
				<div class="modal fade" id="actions-idea-${idee.id}" tabindex="-1" role="dialog" aria-hidden="true">
					<div class="modal-dialog modal-dialog-centered" role="document">
						<div class="modal-content">
							<div class="modal-header">
								<h5 class="modal-title" id="exampleModalLongTitle">Choisissez une action</h5>
								<button type="button" class="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
							</div>
							<div class="modal-body">
								<c:if test="${empty idee.surpriseBy && not idee.isBooked() && not idee.isPartiallyBooked()}">
									<div class="row align-items-center">
										<div class="col-3 pr-0">
											<a href="protected/reserver?idee=${idee.id}&from=/${identic_call_back}" class="img idea_reserver">
												<img src="resources/image/reserver.png" class="clickable" title="Réserver l'idée" width="${action_img_width}px" />
											</a>
										</div>
										<div class="col-9 pl-0 text-left">
											Réserver l'idée
										</div>
									</div>
									<div class="row align-items-center">
										<div class="col-3 pr-0">
											<a href="protected/sous_reserver?idee=${idee.id}" class="img">
												<img src="resources/image/sous_partie.png" class="clickable" title="Réserver une sous-partie de l'idée" width="${action_img_width}px" />
											</a>
										</div>
										<div class="col-9 pl-0 text-left">
											Réserver une sous-partie de l'idée
										</div>
									</div>
									<div class="row align-items-center">
										<div class="col-3 pr-0">
											<a href="protected/create_a_group?idee=${idee.id}" class="img">
												<img src="resources/image/grouper.png" class="clickable" title="Créer un groupe" width="${action_img_width}px" />
											</a>
										</div>
										<div class="col-9 pl-0 text-left">
											Créer un groupe
										</div>
									</div>
								</c:if>
								<c:if test="${empty idee.surpriseBy}">
									<div class="row align-items-center">
										<div class="col-3 pr-0">
											<a href="protected/est_a_jour?idee=${idee.id}&from=/${identic_call_back}" class="img idea_est_a_jour">
												<img src="resources/image/a_jour.png" class="clickable" title="Demander si c'est à jour." width="${action_img_width}px" />
											</a>
										</div>
										<div class="col-9 pl-0 text-left">
											Demander si c'est à jour
										</div>
									</div>
									<div class="row align-items-center">
										<div class="col-3 pr-0">
											<a href="protected/idee_questions?idee=${idee.id}" class="img">
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
										<a href="protected/idee_commentaires?idee=${idee.id}" class="img">
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
				<div class="row justify-content-start">
					<div class="col-auto pr-0">${idee.priorite.image}</div>
					<c:if test="${not empty idee.category}">
					<div class="col-auto px-0">
						<img src="resources/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" width="${action_img_width}px" />
					</div>
					</c:if>
					<c:choose>
						<c:when test="${not empty idee.surpriseBy}">
						<div class="col-auto px-0">
							<img src="resources/image/surprise.png" title="Idée surprise" width="${action_img_width}px" />
						</div>
						</c:when>
						<c:when test="${idee.isBooked()}">
							<c:choose>
								<c:when test="${not empty idee.bookingOwner}">
									<c:choose>
										<c:when test="${userid == idee.bookingOwner.id}">
										<div class="col-auto px-0">
											<a href="protected/dereserver?idee=${idee.id}&from=/${identic_call_back}" class="img idea_dereserver">
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
									<a href="protected/detail_du_groupe?groupid=${idee.groupKDO}" class="img">
										<img src="resources/image/reserve-groupe.png" title="Une réservation de groupe !" alt="Idée réservée par un groupe" width="${action_img_width}px" />
									</a>
								</div>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test="${idee.isPartiallyBooked()}">
						<div class="col-auto px-0">
							<a href="protected/detail_sous_reservation?idee=${idee.id}" class="img">
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
					<c:if test="${idee.hasAskedIfUpToDate()}">
					<div class="col-auto px-0">
						<img src="resources/image/a_jour.png" title="Vous avez envoyé une demande pour savoir si c'est à jour" alt="Demande est-ce à jour envoyée" width="${action_img_width}px" />
					</div>
					</c:if>
					<c:if test="${idee.hasComment()}">
					<div class="col-auto px-0">
						<a href="protected/idee_commentaires?idee=${idee.id}" class="img">
							<img src="resources/image/commentaires.png" title="Il existe des commentaires sur cette idée" width="${action_img_width}px" />
						</a>
					</div>
					</c:if>
					<c:if test="${idee.hasQuestion()}">
					<div class="col-auto px-0">
						<a href="protected/idee_questions?idee=${idee.id}" class="img">
							<img src="resources/image/questions.png" title="Il existe des questions/réponses sur cette idée" width="${action_img_width}px" />
						</a>
					</div>
					</c:if>
					<c:if test="${is_mobile}">
					<div class="col-auto ml-auto">
						<button class="btn btn-primary mobile_actions" data-toggle="modal" data-target="#actions-idea-${idee.id}">Actions...</button>
					</div>
					</c:if>
					<span class="outer_top_tooltiptext">
						<span class="top_tooltiptext">
							<c:if test="${empty idee.surpriseBy && not idee.isBooked() && not idee.isPartiallyBooked()}">
								<a href="protected/reserver?idee=${idee.id}&from=/${identic_call_back}" class="img idea_reserver">
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
								<a href="protected/est_a_jour?idee=${idee.id}&from=/${identic_call_back}" class="img idea_est_a_jour">
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
											Réservée par vous le ${idee.bookingDate} - <a href="protected/dereserver?idee=${idee.id}&from=/${identic_call_back}" class="idea_dereserver">Annuler</a> !
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
		</ul>
	</c:otherwise>
</c:choose>