<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="exception" value="${requestScope['javax.servlet.error.exception']}"/>
<html><head><title><spring:message code="error.unknown"/></title>
<script>
$(function() {
	$( "#showStackTrace" ).click(function() {
		$( "#stackTrace" ).toggle();
	});
});
</script>
</head>
<body>
<h2><spring:message code="error.unknown.info"/></h2>
<b><spring:message code="error.unknown.log"/> <a href="<c:url value="/help/log"/>" target="_blank" id="getLog" style="text-decoration:underline">riv-application.log</a></b><br/><br/>

<p>RuralInvest version: ${version}</p>

<a id="showStackTrace"><spring:message code="error.moreDetails"/></a>
<div id="stackTrace" style="display:none;">
	${exception}
	<c:forEach items="${exception.stackTrace}" var="element">
	    <c:out value="${element}"/>
	</c:forEach>
</div>
</body></html>
