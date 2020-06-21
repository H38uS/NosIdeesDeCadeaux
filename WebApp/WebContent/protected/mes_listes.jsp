<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal_protected>
    <jsp:body>
        <div id="resultPlaceholderForShortcuts">
        </div>
        <div></div>
        <div class="row justify-content-around">
            <div class="col-12">
                <div class="container">
                    <div id="resultPlaceholderForLists">
                    </div>
                    <script src="resources/js/afficher_liste_idees.js" type="text/javascript"></script>
                    <script>
                        displayUsersIdeasList("${identic_call_back}", "${call_back}");
                    </script>
                </div>
            </div>
        </div>
    </jsp:body>
</t:normal_protected>