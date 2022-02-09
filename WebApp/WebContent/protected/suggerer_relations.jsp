<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<t:normal_protected>
    <jsp:body>
        <h3>Rechercher des personnes à suggérer à ${user.name}</h3>
        <div>
            <div class="form-inline">
                <div class="row align-items-center mx-0">
                    <div class="col-auto d-none d-md-inline-block">
                        <label for="name">Nom / Email de la personne</label>
                    </div>
                    <div class="col-7 col-md-auto mx-0">
                        <input class="form-control" type="text" name="name" id="name" value="${name}" />
                    </div>
                    <div class="col-5 col-md-auto">
                        <button class="btn btn-primary" type="submit" id="submit_filter">Filtrer</button>
                    </div>
                </div>
            </form>
        </div>
        <div id="res_possible_suggestions"></div>
        <script src="resources/js/suggest_relation.js" type="text/javascript"></script>
    </jsp:body>
</t:normal_protected>