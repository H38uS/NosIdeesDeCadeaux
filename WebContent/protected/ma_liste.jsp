<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
		<jsp:body>
		<h2>Ajouter une nouvelle idée</h2>
		<div>
			<form action="protected/ma_liste?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
				<table id="ma_liste_table_ajouter">
					<tr>
						<td colspan="2">
							<div class="button_container">
								<div id="ma_liste_deplier" class="button">Cliquer pour déplier</div>
							</div>
						</td>
					</tr>
					<tr class="hidden" >
						<td><label for="text">Le texte de l'idée</label></td>
						<td><textarea id="text" name="text" cols="70" rows="6"></textarea></td>
					</tr>
					<tr class="hidden" >
						<td><label for="type">Type</label></td>
						<td>
							<select id="type" name="type">
								<option value="">Sélectionnez un type</option>
								<c:forEach var="type" items="${types}">
									<option value="${type.name}">${type.alt}</option>
								</c:forEach>
								<option value="">Autre</option>
							</select>
						</td>
					</tr>
					<tr class="hidden" >
						<td><label for="priority">Priorité</label></td>
						<td>
						<select id="priority" name="priority">
							<option value="1">Sélectionnez une priorité</option>
							<c:forEach var="priorite" items="${priorites}">
								<option value="${priorite.id}">${priorite.name}</option>
							</c:forEach>
						</select>
						</td>
					</tr>
					<tr class="hidden" >
						<td>Fichier Choisi</td>
						<td>
							<span id="newImage" class="input"></span>
						</td>
					</tr>
					<tr class="hidden" >
						<td>
							<input id="imageFile" name="image" type="file" accept="image/jpg, image/jpeg, image/png" />
						</td>
						<td>
							<label for="imageFile" class="custom-file-upload">Ajouter une image</label>
						</td>
					</tr>
					<tr class="hidden" >
						<td colspan="2" align="center">
							<input type="submit" name="submit" id="submit" value="Ajouter" />
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div class="errors">
			<c:if test="${fn:length(errors) > 0}">
				<p>Des erreurs ont empêché la création de cette nouvelle idée:</p>
				<ul>
					<c:forEach var="error" items="${errors}">
						<li>${error}</li>
					</c:forEach>
				</ul>
			</c:if>
		</div>
		<h2>Ma liste de cadeaux</h2>
		<c:if test="${fn:length(idees) > 0}">
		<c:choose>
			<c:when test="${is_mobile}">
				<div class="ideas_square_container">
					<c:forEach var="idee" items="${idees}">
						<c:if test="${empty idee.surpriseBy}">
						<div class="idea_square">
							<table>
								<tr>
									<td>
										<span>${idee.priorite.image}</span>
										<c:if test="${not empty idee.category}">
											<img src="resources/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}"
																								width="${action_img_width}px" />
										</c:if>
										<c:if test="${idee.hasQuestion()}">
											<a href="protected/idee_questions?idee=${idee.id}" class="img">
												<img src="resources/image/questions.png" title="Il existe des questions/réponses sur cette idée"
																								width="${action_img_width}px" />
											</a>
										</c:if>
									</td>
								</tr>
								<tr>
									<c:choose>
										<c:when test="${not empty idee.image}">
											<td>
												<div>
													<a href="${ideas_pictures}/${idee.imageSrcLarge}" class="thickbox img">
														<img src="${ideas_pictures}/${idee.imageSrcSmall}" width="150" />
													</a>
												</div>
											</td>
											<td class="idea_square_text">
												${idee.html}
											</td>
										</c:when>
										<c:otherwise>
											<td colspan="2" class="idea_square_text">
												${idee.html}
											</td>
										</c:otherwise>
									</c:choose>
								</tr>
							</table>
							<span class="outer_top_tooltiptext">
								<span class="top_tooltiptext">
									<a href="protected/modifier_idee?id=${idee.id}" class="img">
										<img src="resources/image/modifier.png"
											 title="Modifier cette idée"
											 width="${action_img_width}px" />
									</a>
									<a href="protected/remove_an_idea?ideeId=${idee.id}" class="img idea_remove">
										<img src="resources/image/supprimer.png"
											 title="Supprimer cette idée"
											 width="${action_img_width}px" />
									</a>
									<a href="protected/idee_questions?idee=${idee.id}" class="img">
										<img src="resources/image/questions.png" title="Voir les questions existantes" width="${action_img_width}px" />
									</a>
									<a href="protected/je_le_veux_encore?idee=${idee.id}" class="img">
										<img src="resources/image/encore.png" title="J'ai déjà reçu cette idée, mais je la veux à nouveau ou je veux la suite." height="${action_img_width}px" />
									</a>
								</span>
							</span>
							<div class="idea_square_modif_date" >
								Mise à jour le ${idee.modificationDate}.
							</div>
						</div>
						</c:if>
					</c:forEach>
				</div>
			</c:when>
			<c:otherwise>
				<ul class="ideas_square_container">
					<c:forEach var="idee" items="${idees}">
						<c:if test="${empty idee.surpriseBy}">
							<li class="idea_square top_tooltip">
							<div>
								<div class="left">
									<span>${idee.priorite.image}</span>
									<c:if test="${not empty idee.category}">
										<img src="resources/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}"
																							width="${action_img_width}px" />
									</c:if>
									<c:if test="${idee.hasQuestion()}">
										<a href="protected/idee_questions?idee=${idee.id}" class="img">
											<img src="resources/image/questions.png" title="Il existe des questions/réponses sur cette idée"
																							width="${action_img_width}px" />
										</a>
									</c:if>
									<span class="outer_top_tooltiptext">
										<span class="top_tooltiptext">
											<a href="protected/modifier_idee?id=${idee.id}" class="img">
												<img src="resources/image/modifier.png"
													 title="Modifier cette idée"
													 width="${action_img_width}px" />
											</a>
											<a href="protected/remove_an_idea?ideeId=${idee.id}" class="img idea_remove">
												<img src="resources/image/supprimer.png"
													 title="Supprimer cette idée"
													 width="${action_img_width}px" />
											</a>
											<a href="protected/idee_questions?idee=${idee.id}" class="img">
												<img src="resources/image/questions.png" title="Voir les questions existantes" width="${action_img_width}px" />
											</a>
											<a href="protected/je_le_veux_encore?idee=${idee.id}" class="img">
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
										<a href="${ideas_pictures}/${idee.imageSrcLarge}" class="thickbox img">
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
			</c:otherwise>
		</c:choose>
		</c:if>
	</jsp:body>
</t:normal_protected>