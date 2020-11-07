<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
        <h2>Partager ce <a href="protected/detail_du_groupe?groupid=${group.id}">groupe</a></h2>
        <div id="suggestArea"></div>
        <h2>Rappel de l'idée</h2>
        <div>
            <div id="idea_placeholder"></div>
            <script>
                refreshIdea($("#idea_placeholder"), ${idee.id});
            </script>
        </div>
        <div id="groupDetail">
        </div>
        <script src="resources/js/group.js" type="text/javascript"></script>
        <script>
            refreshPossibleSuggestion(); // Showing the suggestion possibilities
            refreshGroup(false); // hiding the suggest link
        </script>
    </jsp:body>
</t:normal_protected>