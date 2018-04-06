<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
    <body>
    	<div id="container">
    	<header>
			<c:choose>
			<c:when test="${is_mobile}">
				<span class="menu_icon"><img src="resources/image/menu.png" /></span>
				<img src="resources/image/header_index.png" />
				<div id="menu_right_connection"><a href="protected/index.jsp">Se connecter</a></div>
				<div id="logo_text">
					Créer et partager vos envies de cadeaux avec toute votre famille et vos amis
				</div>
				<div id="menu_content">
					<span class="menu_icon"><img src="resources/image/back.png" /></span>
					<div><a href="public/index.jsp" class="fl_green">Accueil</a></div>
					<div><a href="protected/index.jsp" class="fl_yellow">Se connecter</a></div>
					<div><a href="public/creation_compte.jsp" class="fl_blue">Créer un compte</a></div>
					<div><a href="public/comment_ca_marche.jsp" class="fl_purple">Comment ça marche ?</a></div>
				</div>
			</c:when>
			<c:otherwise>
	    		<div id="logo">
		    		<img src="resources/image/header_index.png" ></img>
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
			</c:otherwise>
			</c:choose>
    	</header>
    	<div id="content">
	        <jsp:doBody/>
    	</div>
    	<footer>&#9400; 2016 NosIdeesCadeaux - Tous droits réservés - Conditions générales d'utilisation - <a href="public/remerciements.jsp">Remerciements</a></footer>
    	</div>
    </body>