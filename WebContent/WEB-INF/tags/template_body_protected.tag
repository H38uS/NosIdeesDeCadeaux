<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
    <body>
    	<div id="container">
    	<header>
    		<div id="logo">
	    		<img src="public/image/header_index.png" />
	    		<span>
		    		Bonjour, <c:out value="${emailorname}" /> - <a href="my_logout">me deconnecter.</a>
		    		Accéder à <a href="protected/mon_compte">mon compte.</a>
		    		<c:if test="${notif_count > 0}">
		    			<br/>Vous avez <a href="protected/mes_notifications">${notif_count} notifications</a> !
		    		</c:if>
	    		</span>
    		</div>
    		<div id="logo_text">
    			Créer et partager vos envies de cadeaux avec toute la famille et vos amis
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
    	<div id="content">
	        <jsp:doBody/>
    	</div>
    	<footer>&#9400; 2016 NosIdeesCadeaux - Tous droits réservés - Conditions générales d'utilisation</footer>
    	</div>
    </body>