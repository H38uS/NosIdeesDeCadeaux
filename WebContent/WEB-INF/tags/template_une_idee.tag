<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

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
		<c:if test="${not empty idee.modificationDate}">
			<div class="idea_square_modif_date" >
				${idee.modificationDate}
			</div>
		</c:if>
	</div>
	</li>
</ul>