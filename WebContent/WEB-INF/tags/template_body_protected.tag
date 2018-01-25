<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
    <body>
    	<div id="container">
    	<header>
    		<div id="logo">
	    		<img src="public/image/header_index.png" />
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
    	</header>
		<script type="text/javascript">
			$(document).ready(function() {
				$("#header_name").autocomplete({
					source : "protected/service/name_resolver",
					minLength : 2,
					select : function(event, ui) {
						$("#header_name").val(ui.item.value);
						$("#afficherliste").submit();
						return false;
					}
				});
			});
		</script>
		<div id="content">
	        <jsp:doBody/>
    	</div>
    	<footer>&#9400; 2016 NosIdeesCadeaux - Tous droits réservés - Conditions générales d'utilisation</footer>
    	</div>
    </body>