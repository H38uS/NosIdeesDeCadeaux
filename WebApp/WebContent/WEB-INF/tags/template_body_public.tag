<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
	<body>
		<div id="container">
		<header>
			<nav class="navbar navbar-expand-sm navbar-dark bg-dark">
				<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#menu_content" aria-controls="menu_content" aria-expanded="false" aria-label="Toggle navigation">
					<span class="navbar-toggler-icon"></span>
				</button>
				<div class="mr-auto ml-2">
					<img src="resources/image/mobile_header_index.png" />
				</div>
			</nav>
			<nav class="navbar navbar-dark bg-dark pt-0">
				<div class="collapse navbar-collapse" id="menu_content">
					<ul class="navbar-nav mr-auto mt-lg-0 menu_mobile">
						<li class="nav-item m-2">
							<a href="public/index.jsp" class="btn btn-light">Accueil</a>
						</li>
						<li class="nav-item m-2">
							<a href="protected/index.jsp" class="btn btn-light">Se connecter</a>
						</li>
						<li class="nav-item m-2">
							<a href="public/creation_compte.jsp" class="btn btn-light">Créer un compte</a>
						</li>
						<li class="nav-item m-2">
							<a href="public/comment_ca_marche.jsp" class="btn btn-light">Comment ça marche ?</a>
						</li>
					</ul>
				</div>
			</nav>
			
			<div class="container-fluid">
				<div class="row align-items-center justify-content-center mx-0">
					<div class="col-12 col-md-auto mr-auto huge w-100">Créer et partager vos envies de cadeaux avec toute votre famille et vos amis</div>
					<div class="col-auto pt-2 pt-md-0 d-block d-sm-none">
						<a href="protected/index.jsp" class="btn btn-primary" role="button" >Se connecter</a>
					</div>
				</div>
			</div>

			<nav class="navbar navbar-expand-sm py-0 mt-2 menu">
				<div class="collapse navbar-collapse">
					<ul class="navbar-nav mr-auto mt-lg-0 menu">
						<li class="nav-item">
							<a href="public/index.jsp" class="fl_green">Accueil</a>
						</li>
						<li class="nav-item">
							<a href="protected/index.jsp" class="fl_yellow">Se connecter</a>
						</li>
						<li class="nav-item">
							<a href="public/creation_compte.jsp" class="fl_blue">Créer un compte</a>
						</li>
						<li class="nav-item">
							<a href="public/comment_ca_marche.jsp" class="fl_purple">Comment ça marche ?</a>
						</li>
					</ul>
				</div>
			</nav>
		</header>
		<div id="content" class="container-fluid" >
			<jsp:doBody/>
		</div>
		<footer class="mt-5">
			&#9400; 2020 NosIdeesCadeaux ${application_version} - Tous droits réservés - Conditions générales d'utilisation - <a href="public/remerciements.jsp">Remerciements</a>
		</footer>
		</div>
	</body>