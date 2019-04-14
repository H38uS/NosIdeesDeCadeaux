<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
	<body>
		<div id="container">
		<header>
			<nav class="navbar navbar-expand-lg navbar-dark bg-dark px-0 pb-0">
				<div class="container-fluid">
					<div class="row align-items-center justify-content-end mx-0 w-100">
						<div class="col-auto">
							<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#menu_content" aria-controls="menu_content" aria-expanded="false" aria-label="Toggle navigation">
								<span class="navbar-toggler-icon"></span>
							</button>
						</div>
						<div class="col-auto mr-auto px-0">
							<img src="resources/image/mobile_header_index.png" />
						</div>
						<div class="col-auto justify-content-end d-none d-lg-flex">
							<div class="container">
								<div class="row align-items-center">
									Bonjour, 
									<c:if test="${ not is_mobile}">
									<img id="connected_user_logo" src="protected/files/uploaded_pictures/avatars/${connected_user.avatarSrcSmall}" alt="" style="height:30px;" />
									</c:if>
									${connected_user.name} 
									<c:if test="${not empty initial_connected_user}">
										(depuis le compte de ${initial_connected_user.name},&nbsp;<a href="protected/sorti_enfant">y retourner</a>)
									</c:if> -&nbsp;<a href="<c:url value="/logout" />">me deconnecter</a>
								</div>
								<div class="row align-items-center">
									Accéder à &nbsp;<a href="protected/mon_compte">mon compte</a>&nbsp;
								</div>
							</div>
						</div>
						<div class="col-auto mx-auto my-3 my-sm-0 mx-md-0 justify-content-md-end  d-md-flex">
							<c:if test="${is_mobile}">
								<img id="connected_user_logo" src="protected/files/uploaded_pictures/avatars/${connected_user.avatarSrcSmall}" alt="" style="height:50px;" />
							</c:if>
							<a href="protected/mes_notifications" class="btn btn-secondary ml-2" style="color:white" >
								Notifications <span id="my_notif_count" class="badge badge-light">${notif_count}</span>
							</a>
						</div>
					</div>
				</div>
			</nav>
			<nav class="navbar navbar-dark bg-dark py-0 pb-md-2">
				<div class="collapse navbar-collapse" id="menu_content">
					<ul class="navbar-nav mr-auto mt-lg-0 menu_mobile">
						<li class="nav-item m-2">
							<a href="protected/index" class="btn btn-light">Accueil</a>
						</li>
						<li class="nav-item m-2">
							<a href="protected/ma_liste" class="btn btn-light">Compléter ma liste</a>
						</li>
						<li class="nav-item m-2">
							<a href="protected/mes_listes" class="btn btn-light">Afficher mes listes partagées</a>
						</li>
						<li class="nav-item m-2">
							<a href="protected/afficher_reseau?id=${connected_user.id}" class="btn btn-light">Mes amis</a>
						</li>
						<li class="nav-item m-2">
							<a href="protected/rechercher_personne.jsp" class="btn btn-light">Ajouter un ami</a>
						</li>
						<li class="nav-item m-2">
							<a href="protected/mon_compte" class="btn btn-light">Mon compte</a>
						</li>
						<c:if test="${is_admin}">
						<li class="nav-item m-2">
							<a href="protected/administration/administration" class="btn btn-light">Administration</a>
						</li>
						</c:if>
						<li class="nav-item m-2">
						<c:choose>
							<c:when test="${not empty notif_count}">
								<a href="protected/mes_notifications" class="btn btn-light">Mes notifications (${notif_count})</a>
							</c:when>
							<c:otherwise>
								<a href="protected/mes_notifications" class="btn btn-light">Mes notifications</a>
							</c:otherwise>
						</c:choose>
						</li>
						<c:if test="${not empty initial_connected_user}">
						<li class="nav-item m-2">
							<a href="protected/sorti_enfant" class="btn btn-light">Reconnexion en ${initial_connected_user.name}</a>
						</li>
						</c:if>
						<li class="nav-item m-2">
							<a href="<c:url value="/logout" />" class="btn btn-light">Se déconnecter</a>
						</li>
					</ul>
				</div>
			</nav>

			<div class="container-fluid">
				<div class="row align-items-center justify-content-center mx-0">
					<div class="col-12 col-md-5 col-lg-6 col-xl-7 mr-auto huge w-100 d-none d-sm-block pb-sm-2">Créer et partager vos envies de cadeaux avec toute votre famille et vos amis</div>
					<div class="col-12 col-sm-auto pt-md-0">
						<form id="afficherliste" class="form-inline justify-content-center justify-content-md-end" method="POST" action="protected/afficher_listes">
							<input type="text" class="form-control mx-2" name="name" id="header_name" placeholder="Entrer un nom ou un email" />
							<button type="submit" class="btn btn-primary d-none d-sm-block">Rechercher !</button>
						</form>
					</div>
				</div>
			</div>

			<nav class="navbar navbar-expand-lg py-0 mt-2 mt-xl-3 menu">
				<div class="collapse navbar-collapse">
					<ul class="navbar-nav mr-auto mt-lg-0 menu">
						<li class="nav-item">
							<a href="protected/index" class="fl_green">Accueil</a>
						</li>
						<li class="nav-item">
							<a href="protected/ma_liste" class="fl_yellow">Compléter ma liste</a>
						</li>
						<li class="nav-item">
							<a href="protected/mes_listes" class="fl_blue">Afficher mes listes partagées</a>
						</li>
						<li class="nav-item">
							<a href="protected/afficher_reseau?id=${connected_user.id}" class="fl_purple">Mes amis</a>
						</li>
						<li class="nav-item">
							<a href="protected/rechercher_personne.jsp" class="fl_purple">Ajouter un ami</a>
						</li>
						<c:if test="${is_admin}">
						<li class="nav-item">
							<a href="protected/administration/administration" class="fl_blue">Administration</a>
						</li>
						</c:if>
					</ul>
				</div>
			</nav>
			<c:choose>
				<c:when test="${is_mobile}">
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
							}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
								return $( "<li class=\"ui-menu-item\"></li>" )
									.data( "item.autocomplete", item )  
									.append( '<div class="ui-menu-item-wrapper"> <div class="row align-items-center"><div class="col-4 col-sm-3 col-md-2 center"><img class="avatar" src="' + item.imgsrc + '"/></div><div class="col-8 col-md-9">' + item.value + '</div></div></div>')
									.appendTo( ul );
							};
						});
					</script>
				</c:when>
				<c:otherwise>
					<script type="text/javascript">
						$(document).ready(function() {
							$("#header_name").autocomplete({
								source : "protected/service/name_resolver",
								minLength : 2,
								position: { my : "right top", at: "right bottom", of : "#header_name" },
								select : function(event, ui) {
									$("#header_name").val(ui.item.email);
									$("#afficherliste").submit();
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
				</c:otherwise>
			</c:choose>
		</header>
		<div id="loading_message_container"><div id="loading_message_div"></div></div>
		<div id="mobile_res_search" class="mobile_res_search word-break-all">&nbsp;</div>
		<div id="content">
			<jsp:doBody/>
		</div>
		<footer>&#9400; 2016 NosIdeesCadeaux - Tous droits réservés - Conditions générales d'utilisation - <a href="public/remerciements.jsp">Remerciements</a></footer>
		</div>
	</body>