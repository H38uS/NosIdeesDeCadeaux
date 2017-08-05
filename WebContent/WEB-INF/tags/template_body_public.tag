<%@ tag language="java" pageEncoding="UTF-8"%>
    <body>
    	<div id="container">
    	<header>
    		<div id="logo">
	    		<img src="public/image/header_index.png" ></img>
    		</div>
    		<div id="logo_text">
    			Créer et partager vos envies de cadeaux avec toute la famille et vos amis
    		</div>
    		<ul class="menu">
    			<li><a href="public/index.jsp">Accueil</a></li>
    			<li><a href="protected/index.jsp">Se connecter</a></li>
    			<li><a href="public/creation_compte.jsp">Créer un compte</a></li>
    			<li><a href="public/comment_ca_marche.jsp">Comment ça marche ?</a></li>
    		</ul>
    	</header>
    	<div id="content">
	        <jsp:doBody/>
    	</div>
    	<footer>&#9400; 2016 NosIdeesCadeaux - Tous droits réservés - Conditions générales d'utilisation</footer>
    	</div>
    </body>