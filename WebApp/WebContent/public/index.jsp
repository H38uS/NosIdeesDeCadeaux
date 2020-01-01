<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@  taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:index>
	<jsp:body>
		<c:choose>
			<c:when test="${is_mobile}">
				<div class="container-fluid py-3">
					<div class="row">
						Envie de nous rejoindre ? Découvrez l'univers des cadeaux qui font plaisir !
					</div>
				</div>
				<div class="container">
					<div class="row">
						<div class="col py-5">
							<a href="protected/index" class="btn btn-primary"><span class="fl_green" role="button" >Se </span><span class="fl_yellow"> Connecter</span></a>
						</div>
						<div class="col py-5">
							<a href="public/creation_compte.jsp" class="btn btn-outline-secondary" role="button"><span class="fl_blue">Créer un </span><span class="fl_purple">Compte</span></a>
						</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="indexbox">
					<ul>
						<li>
							<a href="protected/index"><span class="fl_green">Se </span><span class="fl_yellow"> Connecter</span></a>
						</li>
						<li>
							<a href="public/creation_compte.jsp"><span class="fl_blue">Créer un </span><span class="fl_purple">Compte</span></a>
						</li>
					</ul>
				</div>
				<div id="middleindexbox">
					Envie de nous rejoindre ? Découvrez l'univers des cadeaux qui font plaisir !
				</div>
			</c:otherwise>
		</c:choose>
	</jsp:body>
</t:index>