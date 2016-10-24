<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="title" %><%@ attribute name="titleKey" %><%@ attribute name="pager" %>
<h2>
	<c:choose>
		<c:when test="${title != null}"><c:out value="${title}"/></c:when>
		<c:when test="${titleKey != null}"><spring:message code="${titleKey}"/></c:when>
	</c:choose>
</h2>
<c:if test="${not empty pager}"><div style="text-align:right;"><spring:message code="paging.size"/><select id="pagerCount" name="pagerCount" onchange="javascript:(pagerCount_change());">
	<option value="10" <c:if test="${user.pageSize eq 10}">selected="selected"</c:if>>10</option><option value="25" <c:if test="${user.pageSize eq 25}">selected="selected"</c:if>>25</option><option value="50" <c:if test="${user.pageSize eq 50}">selected="selected"</c:if>>50</option><option value="100" <c:if test="${user.pageSize eq 100}">selected="selected"</c:if>>100</option>
</select></div></c:if>
<div class="tableOuter"><jsp:doBody/></div>