<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="title" %><%@ attribute name="titleEval" %><%@ attribute name="titleKey" %>
<div class="tableContainerOuter">
	<div class="dataTitle">
		<c:choose>
			<c:when test="${title != null}"><c:out value="${title}"/></c:when>
			<c:when test="${titleEval != null}"><c:set var="evalTitle"><c:out value="${titleEval}"/></c:set><c:out value="${evalTitle}"/></c:when>
			<c:when test="${titleKey != null}"><spring:message code="${titleKey}"/></c:when>
		</c:choose>
	</div>
	<div class="tableContainerInner"><jsp:doBody/></div>
	<div class="tableContainerBottom"></div>
</div>