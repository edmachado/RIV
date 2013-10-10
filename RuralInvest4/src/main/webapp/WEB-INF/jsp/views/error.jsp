<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:set var="exception" value="${requestScope['javax.servlet.error.exception']}"/>
<html><head><title><spring:message code="error.unknown"/></title></head>
<body>
<h2><spring:message code="error.unknown.info"/></h2>
<b><spring:message code="error.unknown.log"/> <a href="<c:url value="/help/log"/>" target="_blank" id="getLog" style="text-decoration:underline">riv-application.log</a></b><br/><br/>
${exception}
<c:forEach items="${exception.stackTrace}" var="element">
    <c:out value="${element}"/>
</c:forEach>
</body></html>
