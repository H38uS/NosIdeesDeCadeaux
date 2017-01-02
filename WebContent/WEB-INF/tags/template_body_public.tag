<%@ tag language="java" pageEncoding="UTF-8"%>
    <body>
    	<div id="container">
    	<header>
    		<img src="public/image/header_index.png" ></img>
    		<ul class="menu">
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