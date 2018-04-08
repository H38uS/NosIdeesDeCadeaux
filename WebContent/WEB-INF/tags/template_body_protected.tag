<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
    <body>
    	<div id="container">
    	<header>
			<c:choose>
			<c:when test="${is_mobile}">
				<span class="menu_icon"><img src="resources/image/menu.png" /></span>
				<img src="resources/image/header_index.png" />
				<div id="right_search_field" class="ui-widget">
					<form id="afficherliste" method="POST" action="protected/afficher_listes">
						<input type="text" name="name" id="header_name" placeholder="Un nom ou un email" />
						<input type="submit" value="Go" />
					</form>
				</div>
				<div id="menu_content">
					<span class="menu_icon"><img src="resources/image/back.png" /></span>
					<div>
						<a href="protected/index" class="fl_green">Accueil</a>
					</div>
					<div>
						<a href="protected/ma_liste" class="fl_yellow">Compléter ma liste</a>
					</div>
					<div>
						<a href="protected/mes_listes" class="fl_blue">Afficher mes listes partagées</a>
					</div>
					<div>
						<a href="protected/afficher_reseau?id=${userid}" class="fl_purple">Mes amis</a>
					</div>
					<div>
						<a href="protected/rechercher_personne.jsp" class="fl_purple">Ajouter un ami</a>
					</div>
					<div>
						<a href="protected/mon_compte" class="fl_blue">Mon compte</a>
					</div>
					<div>
						<a href="protected/mes_notifications" class="fl_yellow">Mes notifications (${notif_count})</a>
					</div>
					<div>
						<a href="<c:url value="/logout" />" class="fl_green">Se déconnecter</a>
					</div>
				</div>
				<script type="text/javascript">
					$(document).ready(function() {
						jQuery.ui.autocomplete.prototype._resizeMenu = function () {
							var ul = this.menu.element;
							ul.outerWidth(
									Math.max( $("#mobile_res_search").outerWidth(), this.element.outerWidth())
								);
						}
						$("#header_name").autocomplete({
							source : "protected/service/name_resolver",
							minLength : 2,
							appendTo: "#mobile_res_search",
							position: { my : "left top", at: "left top", of : "#mobile_res_search" },
							select : function(event, ui) {
								$("#header_name").val(ui.item.email);
								$("#afficherliste").submit();
								return false;
							}
						});
					});
				</script>
			</c:when>
			<c:otherwise>
	    		<div id="logo">
		    		<img src="resources/image/header_index.png" />
		    		<span>
			    		Bonjour, ${emailorname} - <a href="<c:url value="/logout" />">me deconnecter.</a>
			    		Accéder à <a href="protected/mon_compte">mon compte.</a>
			    		<c:if test="${notif_count > 0}">
			    			<br/>Vous avez <a href="protected/mes_notifications">${notif_count} notifications</a> !
			    		</c:if>
			    		<div id="right_search_field" class="ui-widget">
			    			<form id="afficherliste" method="POST" action="protected/afficher_listes">
			    				<input type="text" name="name" id="header_name" placeholder="Entrez un nom ou un email" />
			    				<input type="submit" value="Rechercher !" />
			    			</form>
			    		</div>
		    		</span>
	    		</div>
	    		<div id="logo_text">
	    			Créer et partager vos envies de cadeaux avec toute votre famille et vos amis
	    		</div>
	    		<ul class="menu">
					<li>
						<a href="protected/index" class="fl_green">Accueil</a>
					</li>
					<li>
						<a href="protected/ma_liste" class="fl_yellow">Compléter ma liste</a>
					</li>
					<li>
						<a href="protected/mes_listes" class="fl_blue">Afficher mes listes partagées</a>
					</li>
					<li>
						<a href="protected/afficher_reseau?id=${userid}" class="fl_purple">Mes amis</a>
					</li>
					<li>
						<a href="protected/rechercher_personne.jsp" class="fl_purple">Ajouter un ami</a>
					</li>
	    		</ul>
				<script type="text/javascript">
					$(document).ready(function() {
						$("#header_name").autocomplete({
							source : "protected/service/name_resolver",
							minLength : 2,
							appendTo: "#mobile_res_search",
							select : function(event, ui) {
								$("#header_name").val(ui.item.email);
								$("#afficherliste").submit();
								return false;
							}
						});
					});
				</script>
			</c:otherwise>
			</c:choose>
    	</header>
		<div id="content">
			<div id="mobile_res_search">&nbsp;</div>
	        <jsp:doBody/>
    	</div>
    	<footer>&#9400; 2016 NosIdeesCadeaux - Tous droits réservés - Conditions générales d'utilisation - <a href="public/remerciements.jsp">Remerciements</a></footer>
    	</div>
    </body>