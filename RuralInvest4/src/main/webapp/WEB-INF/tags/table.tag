<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="title" %><%@ attribute name="titleKey" %>
<h2>
	<c:choose>
		<c:when test="${title != null}"><c:out value="${title}"/></c:when>
		<c:when test="${titleKey != null}"><spring:message code="${titleKey}"/></c:when>
	</c:choose>
</h2><div class="tableOuter"><jsp:doBody/></div>