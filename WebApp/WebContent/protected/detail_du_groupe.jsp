<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
        <h2>L'idée ${idee.owner.myDName} - <a href="protected/voir_liste?id=${idee.owner.id}">Sa liste</a></h2>
        <div class="container">
            <div id="idea_placeholder"></div>
            <script>
                refreshIdea($("#idea_placeholder"), ${idee.id});
            </script>
        </div>
        <div id="groupDetail">
        </div>
        <script src="resources/js/group.js" type="text/javascript"></script>
        <script>
            refreshGroup(true); // adding the suggest link
        </script>
    </jsp:body>
</t:normal_protected>