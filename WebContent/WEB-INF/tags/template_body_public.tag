<%@ tag language="java" pageEncoding="UTF-8"%>
    <body>
    	<div id="container">
    	<header>
    		<div id="logo">
	    		<img src="public/image/header_index.png" ></img>
    		</div>
    		<div id="logo_text">
    			Créer et partager vos envies de cadeaux avec toute votre famille et vos amis
    		</div>
    		<ul class="menu">
    			<li><a href="public/index.jsp" class="fl_green">Accueil</a></li>
    			<li><a href="protected/index.jsp" class="fl_yellow">Se connecter</a></li>
    			<li><a href="public/creation_compte.jsp" class="fl_blue">Créer un compte</a></li>
    			<li><a href="public/comment_ca_marche.jsp" class="fl_purple">Comment ça marche ?</a></li>
    		</ul>
    	</header>
    	<div id="content">
	        <jsp:doBody/>
    	</div>
    	<footer>&#9400; 2016 NosIdeesCadeaux - Tous droits réservés - Conditions générales d'utilisation - <a href="public/remerciements.jsp">Remerciements</a></footer>
    	</div>
    </body>