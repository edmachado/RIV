<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="title" %>
<%@ attribute name="noKey" %>

<c:if test="${empty noKey}">
<c:set var="text"><spring:message code="${text}"/></c:set>
<c:set var="title"><spring:message code="${title}"/></c:set>
</c:if>

<c:set var="text">${fn:replace(text, "'", "\\'")}</c:set>
<c:set var="text">${fn:replace(text, "\"", "\\'\\'")}</c:set>
<c:set var="title">${fn:replace(title, "'", "\\'")}</c:set>
<c:set var="title">${fn:replace(title, "\"", "\\'\\'")}</c:set>

<c:if test="${not empty title}">
	<%--<c:set var="text"><b>${title}</b><br/>${text}</c:set> --%><c:set var="text">${title}. ${text}</c:set>
</c:if>

<a href="#" title="${text}"><img src="<%=request.getContextPath()%>/img/help.gif" width="11" height="11" border="0" vspace="2">&nbsp;<jsp:doBody/></a>