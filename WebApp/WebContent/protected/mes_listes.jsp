<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>

        <c:if test="${not empty idee}">
            Votre idée a bien été créée. Cliquez <a href="protected/ma_liste">ici</a> pour en ajouter d'autres !
            L'idée ajoutée:
            <t:template_une_idee/>
        </c:if>

        <c:if test="${fn:length(entities) gt 1}">
            <c:choose>
                <c:when test="${is_mobile}">
                    <div id="mes_listes_list_users">
                        <c:forEach var="user" items="${entities}">
                            <a href="${identic_call_back}#list_${user.id}">${user.name}</a> |
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div id="mes_listes_list_users" class="d-none d-xl-inline-block col-xl-3">
                        <c:forEach var="user" items="${entities}">
                            <a href="${identic_call_back}#list_${user.id}" class="col-12 p-2">
                                <div class="center">
                                    <img src="protected/files/uploaded_pictures/avatars/${user.avatarSrcSmall}"
                                         style='height:80px' alt=""/>
                                </div>
                                <div class="center">
                                    ${user.name}
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <div class="row justify-content-around">

            <div id="mes_listes_entities_container" class="col-12">

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
								}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
									return $( "<li class=\"ui-menu-item\"></li>" )
									.data( "item.autocomplete", item )  
									.append( '<div class="ui-menu-item-wrapper"> <div class="row align-items-center"><div class="col-4 col-sm-3 col-md-2 center"><img class="avatar" src="' + item.imgsrc + '"/></div><div class="col-8 col-md-9">' + item.value + '</div></div></div>')
									.appendTo( ul );
								};
							});

                        </script>
                        <div class="alert alert-warning">
                            <div class="row align-items-center pb-2">
                                <div class="col-auto">
                                    Vous ne trouvez pas votre bonheur ?<br/> Recherchez une liste particulière :
                                </div>
                                <form id="afficherliste_topmeslistes" class="form-inline" method="POST"
                                      action="protected/afficher_listes">
                                    <input type="text" class="form-control" name="name" id="top_mes_listes_search"
                                           placeholder="Entrez un nom ou un email"/>
                                    <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                                </form>
                            </div>
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
                                            <a class="page-link"
                                               href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="page-item active">
                                            <a class="page-link"
                                               href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
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
                        <c:if test="${connected_user.id == user.id}">

                            <!-- Début idée de la personne -->
                            <div class="container">
                                <h2 id="list_${user.id}" class="breadcrumb mt-4 h2_list">
                                    <div class="row align-items-center">
                                        <div class="col-auto mx-auto my-1">
                                            <img src="protected/files/uploaded_pictures/avatars/${user.avatarSrcSmall}"
                                                 alt="" style="height:50px;"/>
                                        </div>
                                        <div class="mx-1">
                                            <span class="d-none d-lg-inline-block">Mes idées de cadeaux</span>
                                            <span class="d-inline-block d-lg-none">Mes idées</span>
                                        </div>
                                        <div class="mx-auto">
                                            <a href="protected/ma_liste" class="img">
                                                <c:choose>
                                                    <c:when test="${is_mobile}">
                                                        <img src="resources/image/ajouter_champs.png"
                                                             style="margin-left: 10px;margin-top: -2px;"
                                                             class="clickable" title="Je veux plus de cadeaux"
                                                             width="${action_img_width}px"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="resources/image/ajouter_champs.png"
                                                             style="margin-left: 10px;margin-top: 1px;"
                                                             class="clickable" title="Je veux plus de cadeaux"
                                                             width="${action_img_width}px"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </a>
                                        </div>
                                    </div>
                                </h2>
                                <c:if test="${fn:length(user.ideas) > 0}">
                                    <c:forEach var="idea_from_liste" items="${user.ideas}">
                                        <c:if test="${empty idea_from_liste.surpriseBy}">
                                            <div class="idea_square top_tooltip col-lg-12 my-3 px-2">
                                                <div class="p-2">
                                                    <div class="modal fade" id="actions-idea-${idea_from_liste.id}"
                                                         tabindex="-1" role="dialog" aria-hidden="true">
                                                        <div class="modal-dialog modal-dialog-centered" role="document">
                                                            <div class="modal-content">
                                                                <div class="modal-header">
                                                                    <h5 class="modal-title" id="exampleModalLongTitle">
                                                                        Choisissez une action</h5>
                                                                    <button type="button" class="close"
                                                                            data-dismiss="modal" aria-label="Close">
                                                                        <span aria-hidden="true">&times;</span>
                                                                    </button>
                                                                </div>
                                                                <div class="modal-body">
                                                                    <div class="row align-items-center">
                                                                        <div class="col-3">
                                                                            <a href="protected/modifier_idee?id=${idea_from_liste.id}&from=/${identic_call_back}"
                                                                               class="img">
                                                                                <img src="resources/image/modifier.png"
                                                                                     title="Modifier cette idée"
                                                                                     width="${action_img_width}px"/>
                                                                            </a>
                                                                        </div>
                                                                        <div class="col-9 pl-0 text-left">
                                                                            Modifier cette idée
                                                                        </div>
                                                                    </div>
                                                                    <div class="row align-items-center">
                                                                        <div class="col-3">
                                                                            <a href="protected/remove_an_idea?ideeId=${idea_from_liste.id}&from=/${identic_call_back}"
                                                                               class="img idea_remove">
                                                                                <img src="resources/image/supprimer.png"
                                                                                     title="Supprimer cette idée"
                                                                                     width="${action_img_width}px"/>
                                                                            </a>
                                                                        </div>
                                                                        <div class="col-9 pl-0 text-left">
                                                                            Supprimer cette idée
                                                                        </div>
                                                                    </div>
                                                                    <div class="row align-items-center">
                                                                        <div class="col-3">
                                                                            <a href="protected/idee_questions?idee=${idea_from_liste.id}"
                                                                               class="img">
                                                                                <img src="resources/image/questions.png"
                                                                                     title="Voir les questions existantes"
                                                                                     width="${action_img_width}px"/>
                                                                            </a>
                                                                        </div>
                                                                        <div class="col-9 pl-0 text-left">
                                                                            Voir les questions / Ajouter des précisions
                                                                        </div>
                                                                    </div>
                                                                    <div class="row align-items-center">
                                                                        <div class="col-3">
                                                                            <a href="protected/je_le_veux_encore?idee=${idea_from_liste.id}&from=/${identic_call_back}"
                                                                               class="img">
                                                                                <img src="resources/image/encore.png"
                                                                                     title="J'ai déjà reçu cette idée, mais je la veux à nouveau ou je veux la suite."
                                                                                     height="${action_img_width}px"/>
                                                                            </a>
                                                                        </div>
                                                                        <div class="col-9 pl-0 text-left">
                                                                            Annuler toutes les réservations
                                                                        </div>
                                                                    </div>

                                                                </div>
                                                                <div class="modal-footer">
                                                                    <button type="button" class="btn btn-primary"
                                                                            data-dismiss="modal">Fermer
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="row justify-content-start align-items-center pb-2">
                                                        <div class="col-auto pr-0 pl-1">
                                                            ${idea_from_liste.priorite.image}
                                                        </div>
                                                        <c:if test="${not empty idea_from_liste.category}">
                                                            <div class="col-auto px-0">
                                                                <img src="resources/image/type/${idea_from_liste.category.image}"
                                                                     title="${idea_from_liste.category.title}"
                                                                     alt="${idea_from_liste.category.alt}"
                                                                     width="${action_img_width}px"/>
                                                            </div>
                                                        </c:if>
                                                        <c:if test="${idea_from_liste.hasQuestion()}">
                                                            <div class="col-auto px-0">
                                                                <a href="protected/idee_questions?idee=${idea_from_liste.id}"
                                                                   class="img">
                                                                    <img src="resources/image/questions.png"
                                                                         title="Il existe des questions/réponses sur cette idée"
                                                                         width="${action_img_width}px"/>
                                                                </a>
                                                            </div>
                                                        </c:if>
                                                        <c:if test="${is_mobile}">
                                                            <div class="col-auto ml-auto" data-toggle="modal"
                                                                 data-target="#actions-idea-${idea_from_liste.id}">
                                                                <button class="btn btn-primary">Actions...</button>
                                                            </div>
                                                        </c:if>
                                                        <span class="outer_top_tooltiptext">
														<span class="top_tooltiptext">
															<a href="protected/modifier_idee?id=${idea_from_liste.id}&from=/${identic_call_back}"
                                                               class="img">
																<img src="resources/image/modifier.png"
                                                                     title="Modifier cette idée"
                                                                     width="${action_img_width}px"/>
															</a>
															<a href="protected/remove_an_idea?ideeId=${idea_from_liste.id}&from=/${identic_call_back}"
                                                               class="img idea_remove">
																<img src="resources/image/supprimer.png"
                                                                     title="Supprimer cette idée"
                                                                     width="${action_img_width}px"/>
															</a>
															<a href="protected/idee_questions?idee=${idea_from_liste.id}"
                                                               class="img">
																<img src="resources/image/questions.png"
                                                                     title="Voir les questions existantes"
                                                                     width="${action_img_width}px"/>
															</a>
															<a href="protected/je_le_veux_encore?idee=${idea_from_liste.id}&from=/${identic_call_back}"
                                                               class="img">
																<img src="resources/image/encore.png"
                                                                     title="J'ai déjà reçu cette idée, mais je la veux à nouveau ou je veux la suite."
                                                                     height="${action_img_width}px"/>
															</a>
														</span>
													</span>
                                                    </div>
                                                    <div class="row align-items-center">
                                                        <c:if test="${not empty idea_from_liste.image}">
                                                            <div class="col-auto pl-2 pr-2">
                                                                <a href="${ideas_pictures}/${idea_from_liste.imageSrcLarge}"
                                                                   class="thickbox img">
                                                                    <img src="${ideas_pictures}/${idea_from_liste.imageSrcSmall}"
                                                                         width="150"/>
                                                                </a>
                                                            </div>
                                                        </c:if>
                                                        <div class="left col word-break-all px-2">
                                                            ${idea_from_liste.html}
                                                        </div>
                                                    </div>
                                                    <div class="idea_square_modif_date">
                                                        Mise à jour le ${idea_from_liste.modificationDate}.
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${fn:length(user.ideas) == 0}">
                                    <div class="alert alert-primary">
                                        Vous n'avez pas encore d'idées.
                                        Cliquez <a href="protected/ma_liste">ici</a> pour en ajouter.
                                    </div>
                                </c:if>
                            </div>
                        </c:if>

                        <!-- Fin idée de la personne -->

                        <c:if test="${connected_user.id != user.id}">
                            <div class="container">
                                <h2 id="list_${user.id}" class="breadcrumb mt-4 h2_list">
                                    <div class="row align-items-center">
                                        <div class="col-auto mx-auto my-1">
                                            <img src="protected/files/uploaded_pictures/avatars/${user.avatarSrcSmall}"
                                                 alt="" style="height:50px;"/>
                                        </div>
                                        <div class="mx-1">
                                            <span class="d-none d-lg-inline-block">Liste de cadeaux ${user.myDName}</span>
                                            <span class="d-inline-block d-lg-none">${user.name}</span>
                                        </div>
                                        <div class="mx-auto">
                                            <a href="protected/ajouter_idee_ami?id=${user.id}" class="img">
                                                <c:choose>
                                                    <c:when test="${is_mobile}">
                                                        <img src="resources/image/ajouter_champs.png"
                                                             style="margin-left: 10px;margin-top: -2px;"
                                                             class="clickable" title="Lui ajouter une idée"
                                                             width="${action_img_width}px"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="resources/image/ajouter_champs.png"
                                                             style="margin-left: 10px;margin-top: 1px;"
                                                             class="clickable" title="Lui ajouter une idée"
                                                             width="${action_img_width}px"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </a>
                                        </div>
                                    </div>
                                </h2>
                                <c:if test="${fn:length(user.ideas) > 0}">
                                    <c:forEach var="idea_from_liste" items="${user.ideas}">
                                        <div class="idea_square top_tooltip ${idea_from_liste.displayClass} col-lg-12 my-3 px-2">
                                            <div class="p-2">
                                                <div class="modal fade" id="actions-idea-${idea_from_liste.id}"
                                                     tabindex="-1" role="dialog" aria-hidden="true">
                                                    <div class="modal-dialog modal-dialog-centered" role="document">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <h5 class="modal-title" id="exampleModalLongTitle">
                                                                    Choisissez une action</h5>
                                                                <button type="button" class="close" data-dismiss="modal"
                                                                        aria-label="Close">
                                                                    <span aria-hidden="true">&times;</span>
                                                                </button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <c:if test="${not idea_from_liste.isBooked() && not idea_from_liste.isPartiallyBooked()}">
                                                                    <div class="row align-items-center">
                                                                        <div class="col-3 pr-0">
                                                                            <a href="protected/reserver?idee=${idea_from_liste.id}&from=/${identic_call_back}"
                                                                               class="img idea_reserver">
                                                                                <img src="resources/image/reserver.png"
                                                                                     class="clickable"
                                                                                     title="Réserver l'idée"
                                                                                     width="${action_img_width}px"/>
                                                                            </a>
                                                                        </div>
                                                                        <div class="col-9 pl-0 text-left">
                                                                            Réserver l'idée
                                                                        </div>
                                                                    </div>
                                                                    <div class="row align-items-center">
                                                                        <div class="col-3 pr-0">
                                                                            <a href="protected/sous_reserver?idee=${idea_from_liste.id}"
                                                                               class="img">
                                                                                <img src="resources/image/sous_partie.png"
                                                                                     class="clickable"
                                                                                     title="Réserver une sous-partie de l'idée"
                                                                                     width="${action_img_width}px"/>
                                                                            </a>
                                                                        </div>
                                                                        <div class="col-9 pl-0 text-left">
                                                                            Réserver une sous-partie de l'idée
                                                                        </div>
                                                                    </div>
                                                                    <div class="row align-items-center">
                                                                        <div class="col-3 pr-0">
                                                                            <a href="protected/create_a_group?idee=${idea_from_liste.id}"
                                                                               class="img">
                                                                                <img src="resources/image/grouper.png"
                                                                                     class="clickable"
                                                                                     title="Créer un groupe"
                                                                                     width="${action_img_width}px"/>
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
                                                                            <a href="protected/est_a_jour?idee=${idea_from_liste.id}&from=/${identic_call_back}"
                                                                               class="img idea_est_a_jour">
                                                                                <img src="resources/image/a_jour.png"
                                                                                     class="clickable"
                                                                                     title="Demander si c'est à jour."
                                                                                     width="${action_img_width}px"/>
                                                                            </a>
                                                                        </div>
                                                                        <div class="col-9 pl-0 text-left">
                                                                            Demander si c'est à jour
                                                                        </div>
                                                                    </div>
                                                                    <div class="row align-items-center">
                                                                        <div class="col-3 pr-0">
                                                                            <a href="protected/idee_questions?idee=${idea_from_liste.id}"
                                                                               class="img">
                                                                                <img src="resources/image/questions.png"
                                                                                     class="clickable"
                                                                                     title="Poser une question à ${user.name} / voir les existantes"
                                                                                     width="${action_img_width}px"/>
                                                                            </a>
                                                                        </div>
                                                                        <div class="col-9 pl-0 text-left">
                                                                            Poser une question à ${user.name} / voir les
                                                                            existantes
                                                                        </div>
                                                                    </div>
                                                                </c:if>
                                                                <div class="row align-items-center">
                                                                    <div class="col-3 pr-0">
                                                                        <a href="protected/idee_commentaires?idee=${idea_from_liste.id}"
                                                                           class="img">
                                                                            <img src="resources/image/commentaires.png"
                                                                                 title="Ajouter un commentaire / voir les existants"
                                                                                 width="${action_img_width}px"/>
                                                                        </a>
                                                                    </div>
                                                                    <div class="col-9 pl-0 text-left">
                                                                        Ajouter un commentaire / voir les existants
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-primary"
                                                                        data-dismiss="modal">Fermer
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="row justify-content-start align-items-center pb-2">
                                                    <div class="col-auto pr-0 pl-1">${idea_from_liste.priorite.image}
                                                    </div>
                                                    <c:if test="${not empty idea_from_liste.category}">
                                                        <div class="col-auto px-0">
                                                            <img src="resources/image/type/${idea_from_liste.category.image}"
                                                                 title="${idea_from_liste.category.title}"
                                                                 alt="${idea_from_liste.category.alt}"
                                                                 width="${action_img_width}px"/>
                                                        </div>
                                                    </c:if>
                                                    <c:if test="${not empty idea_from_liste.surpriseBy}">
                                                        <div class="col-auto px-0">
                                                            <img src="resources/image/surprise.png"
                                                                 title="Idée surprise" width="${action_img_width}px"/>
                                                        </div>
                                                    </c:if>
                                                    <c:choose>
                                                        <c:when test="${idea_from_liste.isBooked()}">
                                                            <c:choose>
                                                                <c:when test="${idea_from_liste.bookingOwner.isPresent()}">
                                                                    <c:choose>
                                                                        <c:when test="${connected_user.id == idea_from_liste.bookingOwner.get().id}">
                                                                            <div class="col-auto px-0">
                                                                                <a href="?idee=${idea_from_liste.id}"
                                                                                   class="img idea_dereserver">
                                                                                    <img src="resources/image/reserve-moi.png"
                                                                                         title="Une de vos généreuses réservations - Cliquer pour annuler"
                                                                                         alt="Idée réservée par vous"
                                                                                         width="${action_img_width}px"/>
                                                                                </a>
                                                                            </div>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <div class="col-auto px-0">
                                                                                <img src="resources/image/reserve-autre.png"
                                                                                     title="Une réservation d'une autre personne plus rapide..."
                                                                                     alt="Idée réservée par une autre personne"
                                                                                     width="${action_img_width}px"/>
                                                                            </div>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="col-auto px-0">
                                                                        <a href="protected/detail_du_groupe?groupid=${idea_from_liste.groupKDO}"
                                                                           class="img">
                                                                            <img src="resources/image/reserve-groupe.png"
                                                                                 title="Une réservation de groupe !"
                                                                                 alt="Idée réservée par un groupe"
                                                                                 width="${action_img_width}px"/>
                                                                        </a>
                                                                    </div>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:when test="${idea_from_liste.isPartiallyBooked()}">
                                                            <div class="col-auto px-0">
                                                                <a href="protected/detail_sous_reservation?idee=${idea_from_liste.id}"
                                                                   class="img">
                                                                    <img src="resources/image/non-reserve.png"
                                                                         title="Un sous-ensemble de cette idée est réservé. Voyez si vous pouvez compléter !"
                                                                         alt="Sous partie de l'idée réservée"
                                                                         width="${action_img_width}px"/>
                                                                </a>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="col-auto px-0">
                                                                <img src="resources/image/non-reserve.png"
                                                                     title="Cette idée est libre... Faite plaisir en l'offrant !"
                                                                     alt="Idée non réservée"
                                                                     width="${action_img_width}px"/>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:if test="${idea_from_liste.hasAskedIfUpToDate()}">
                                                        <div class="col-auto px-0">
                                                            <img src="resources/image/a_jour.png"
                                                                 title="Vous avez envoyé une demande pour savoir si c'est à jour"
                                                                 alt="Demande est-ce à jour envoyée"
                                                                 width="${action_img_width}px"/>
                                                        </div>
                                                    </c:if>
                                                    <c:if test="${idea_from_liste.hasComment()}">
                                                        <div class="col-auto px-0">
                                                            <a href="protected/idee_commentaires?idee=${idea_from_liste.id}"
                                                               class="img">
                                                                <img src="resources/image/commentaires.png"
                                                                     title="Il existe des commentaires sur cette idée"
                                                                     width="${action_img_width}px"/>
                                                            </a>
                                                        </div>
                                                    </c:if>
                                                    <c:if test="${idea_from_liste.hasQuestion()}">
                                                        <div class="col-auto px-0">
                                                            <a href="protected/idee_questions?idee=${idea_from_liste.id}"
                                                               class="img">
                                                                <img src="resources/image/questions.png"
                                                                     title="Il existe des questions/réponses sur cette idée"
                                                                     width="${action_img_width}px"/>
                                                            </a>
                                                        </div>
                                                    </c:if>
                                                    <c:if test="${is_mobile}">
                                                        <div class="col-auto ml-auto" data-toggle="modal"
                                                             data-target="#actions-idea-${idea_from_liste.id}">
                                                            <button class="btn btn-primary">Actions...</button>
                                                        </div>
                                                    </c:if>
                                                    <span class="outer_top_tooltiptext">
												<span class="top_tooltiptext">
													<c:if test="${not idea_from_liste.isBooked() && not idea_from_liste.isPartiallyBooked()}">
														<a href="protected/reserver?idee=${idea_from_liste.id}&from=/${identic_call_back}"
                                                           class="img idea_reserver">
															<img src="resources/image/reserver.png" class="clickable"
                                                                 title="Réserver l'idée" width="${action_img_width}px"/>
														</a>
														<a href="protected/sous_reserver?idee=${idea_from_liste.id}"
                                                           class="img">
															<img src="resources/image/sous_partie.png" class="clickable"
                                                                 title="Réserver une sous-partie de l'idée"
                                                                 width="${action_img_width}px"/>
														</a>
														<a href="protected/create_a_group?idee=${idea_from_liste.id}"
                                                           class="img">
															<img src="resources/image/grouper.png" class="clickable"
                                                                 title="Créer un groupe" width="${action_img_width}px"/>
														</a>
													</c:if>
													<c:if test="${empty idea_from_liste.surpriseBy}">
														<a href="protected/est_a_jour?idee=${idea_from_liste.id}&from=/${identic_call_back}"
                                                           class="img idea_est_a_jour">
															<img src="resources/image/a_jour.png" class="clickable"
                                                                 title="Demander si c'est à jour."
                                                                 width="${action_img_width}px"/>
														</a>
														<a href="protected/idee_questions?idee=${idea_from_liste.id}"
                                                           class="img">
															<img src="resources/image/questions.png" class="clickable"
                                                                 title="Poser une question à ${user.name} / voir les existantes"
                                                                 width="${action_img_width}px"/>
														</a>
													</c:if>
													<a href="protected/idee_commentaires?idee=${idea_from_liste.id}"
                                                       class="img">
														<img src="resources/image/commentaires.png"
                                                             title="Ajouter un commentaire / voir les existants"
                                                             width="${action_img_width}px"/>
													</a>
												</span>
											</span>
                                                </div>
                                                <div class="row align-items-center">
                                                    <c:if test="${not empty idea_from_liste.image}">
                                                        <div class="col-auto pl-2 pr-2">
                                                            <a href="${ideas_pictures}/${idea_from_liste.imageSrcLarge}"
                                                               class="thickbox img">
                                                                <img src="${ideas_pictures}/${idea_from_liste.imageSrcSmall}"
                                                                     width="150"/>
                                                            </a>
                                                        </div>
                                                    </c:if>
                                                    <div class="left col word-break-all px-2">
                                                        ${idea_from_liste.html}
                                                    </div>
                                                </div>
                                                <div class="idea_square_modif_date">
                                                    Dernière modification le ${idea_from_liste.modificationDate}.<br/>
                                                    <c:if test="${not empty idea_from_liste.surpriseBy}">
                                                        <div>
                                                            <c:choose>
                                                                <c:when test="${idea_from_liste.surpriseBy.id == connected_user.id}">
                                                                    Idée surprise créée le
                                                                    ${idea_from_liste.modificationDate} par vous - la <a
                                                                        href="protected/supprimer_surprise?idee=${idea_from_liste.id}&from=/${identic_call_back}">supprimer</a>.
                                                                </c:when>
                                                                <c:otherwise>
                                                                    Idée surprise créée le
                                                                    ${idea_from_liste.modificationDate} par
                                                                    ${idea_from_liste.surpriseBy.name}.
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                    </c:if>
                                                    <c:choose>
                                                        <c:when test="${idea_from_liste.isBooked()}">
                                                            <c:choose>
                                                                <c:when test="${idea_from_liste.bookingOwner.isPresent()}">
                                                                    <c:choose>
                                                                        <c:when test="${connected_user.id == idea_from_liste.bookingOwner.get().id}">
                                                                            Réservée par vous le ${idea_from_liste.bookingDate} - <a href="?idee=${idea_from_liste.id}" class="idea_dereserver">Annuler</a> !
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            Réservée par
                                                                            ${idea_from_liste.bookingOwner.get().name} le
                                                                            ${idea_from_liste.bookingDate}
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    Réservée par un groupe (créé le
                                                                    ${idea_from_liste.bookingDate}).
                                                                    <a href="protected/detail_du_groupe?groupid=${idea_from_liste.groupKDO}">Voir
                                                                        le détail du groupe</a>.
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:when test="${idea_from_liste.isPartiallyBooked()}">
                                                            Une sous partie de l'idée est actuellement réservée.
                                                            <a href="protected/detail_sous_reservation?idee=${idea_from_liste.id}">Voir
                                                                le détail.</a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            Non réservée.
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${fn:length(user.ideas) == 0}">
                                    <div class="alert alert-primary">
                                        ${user.name} n'a pas encore d'idées. Pour lui en ajouter (surprise ou pas),
                                        cliquer
                                        <a href="protected/ajouter_idee_ami?id=${user.id}">ici</a>
                                        ou sur le plus blanc ci-dessus.
                                    </div>
                                </c:if>
                            </div>
                        </c:if>
                    </c:forEach>

                    <c:if test="${not empty pages}">
                        <div class="my-3">
                            <ul class="pagination justify-content-center">
                                <c:choose>
                                    <c:when test="${current != 1}">
                                        <li class="page-item">
                                            <a class="page-link"
                                               href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="page-item disabled">
                                            <a class="page-link"
                                               href="${call_back}?page=${current-1}${spec_parameters}">Précédent</a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                                <c:forEach var="page" items="${pages}">
                                    <c:choose>
                                        <c:when test="${current != page.numero}">
                                            <li class="page-item">
                                                <a class="page-link"
                                                   href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="page-item active">
                                                <a class="page-link"
                                                   href="${call_back}?page=${page.numero}${spec_parameters}">${page.numero}</a>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:choose>
                                    <c:when test="${current != last}">
                                        <li class="page-item">
                                            <a class="page-link"
                                               href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="page-item disabled">
                                            <a class="page-link"
                                               href="${call_back}?page=${current+1}${spec_parameters}">Suivant</a>
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
                            Vous pouvez entrer un nouveau nom ci-dessous, ou cliquer sur <a href="protected/mes_listes">ce
                            lien</a>
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
								position: { my : "left bottom", at: "left top", of : "#bottom_mes_listes_search" },
								select : function(event, ui) {
									$("#bottom_mes_listes_search").val(ui.item.email);
									$("#afficherliste_bottommeslistes").submit();
									return false;
								}
							}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
								return $( "<li class=\"ui-menu-item\"></li>" )
								.data( "item.autocomplete", item )  
								.append( '<div class="ui-menu-item-wrapper"> <div class="row align-items-center"><div class="col-4 col-sm-3 col-md-2 center"><img class="avatar" src="' + item.imgsrc + '"/></div><div class="col-8 col-md-9">' + item.value + '</div></div></div>')
								.appendTo( ul );
							};
						});
                    </script>
                    <div class="alert alert-warning">
                        <div class="row align-items-center">
                            <c:choose>
                                <c:when test="${fn:length(entities) eq 1}">
                                    <div class="col-auto">
                                        Consultez une autre liste :
                                    </div>
                                    <form id="afficherliste_bottommeslistes" class="form-inline" method="POST"
                                          action="protected/afficher_listes">
                                        <input type="text" class="form-control" name="name"
                                               id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email"/>
                                        <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <div class="col-auto">
                                        Vous ne trouvez pas votre bonheur ?<br/> Recherchez une liste particulière :
                                    </div>
                                    <form id="afficherliste_bottommeslistes" class="form-inline" method="POST"
                                          action="protected/afficher_listes">
                                        <input type="text" class="form-control" name="name"
                                               id="bottom_mes_listes_search" placeholder="Entrez un nom ou un email"/>
                                        <button class="btn btn-primary mx-2" type="submit">Rechercher !</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </jsp:body>
</t:normal_protected>