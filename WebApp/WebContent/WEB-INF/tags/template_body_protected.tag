<%@  taglib  uri="http://java.sun.com/jsp/jstl/sql"  prefix="sql"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ tag language="java" pageEncoding="UTF-8"%>
    <body>
        <div id="container">
        <header>
            <nav class="navbar navbar-expand-lg navbar-dark bg-dark px-0 pb-0">
                <div class="container-fluid px-0 px-lg-2">
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
                        <div class="col-auto mx-auto my-3 my-sm-0 mx-md-0 justify-content-md-end">
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
                            <a href="protected/ajouter_idee" class="btn btn-light">Compléter ma liste</a>
                        </li>
                        <li class="nav-item m-2">
                            <a href="protected/voir_liste?id=${connected_user.id}" class="btn btn-light">Ma liste</a>
                        </li>
                        <li class="nav-item m-2">
                            <a href="protected/mes_reservations.jsp" class="btn btn-light">Mes réservations</a>
                        </li>
                        <li class="nav-item m-2">
                            <a href="protected/afficher_reseau?id=${connected_user.id}" class="btn btn-light">Mes amis</a>
                        </li>
                        <li class="nav-item m-2">
                            <a href="protected/rechercher_personne" class="btn btn-light">Ajouter un ami</a>
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
                <div class="container-fluid px-0 px-lg-2">
                <form id="afficherliste" class="justify-content-center justify-content-md-end" method="POST" action="protected/afficher_listes">
                    <div class="form-row justify-content-end align-items-center mx-0">
                        <div class="col-12 col-lg-5 col-xl-6 d-none d-sm-block pb-sm-2 pl-0 pr-3">
                            Créer et partager vos envies de cadeaux avec toute votre famille et vos amis
                        </div>
                        <div class="col px-0">
                            <input type="text" class="form-control" name="name" id="header_name" placeholder="Entrer un nom ou un email" />
                        </div>
                        <div class="col-auto px-0">
                            <button type="submit" class="btn btn-primary d-none d-sm-block ml-2">Rechercher !</button>
                        </div>
                    </div>
                </form>
                </div>
            </div>

            <nav class="navbar navbar-expand-lg py-0 mt-2 mt-xl-3 menu">
                <div class="collapse navbar-collapse">
                    <ul class="navbar-nav mr-auto mt-lg-0 menu">
                        <li class="nav-item">
                            <a href="protected/index" class="fl_green">Accueil</a>
                        </li>
                        <li class="nav-item">
                            <a href="protected/ajouter_idee" class="fl_yellow">Compléter ma liste</a>
                        </li>
                        <li class="nav-item">
                            <a href="protected/voir_liste?id=${connected_user.id}" class="fl_blue">Ma liste</a>
                        </li>
                        <li class="nav-item">
                            <a href="protected/mes_reservations.jsp" class="fl_yellow">Mes réservations</a>
                        </li>
                        <li class="nav-item">
                            <a href="protected/afficher_reseau?id=${connected_user.id}" class="fl_purple">Mes amis</a>
                        </li>
                        <li class="nav-item">
                            <a href="protected/rechercher_personne" class="fl_purple">Ajouter un ami</a>
                        </li>
                        <c:if test="${is_admin}">
                        <li class="nav-item">
                            <a href="protected/administration/administration" class="fl_blue">Administration</a>
                        </li>
                        </c:if>
                    </ul>
                </div>
            </nav>
        </header>
        <div id="loading_message_container"><div id="loading_message_div"></div></div>
        <div id="mobile_res_search" class="mobile_res_search word-break-all">&nbsp;</div>
        <div id="content">
            <jsp:doBody/>
        </div>
        <footer>&#9400; 2020 NosIdeesCadeaux ${application_version} - Tous droits réservés - <a href="public/remerciements.jsp">Remerciements</a></footer>
        </div>
    </body>