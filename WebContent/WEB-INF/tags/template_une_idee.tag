<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>

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