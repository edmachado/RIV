<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="labelKey" %>
<%@ attribute name="label" %>
<%@ attribute name="helpText" %>
<%@ attribute name="helpTitle" %>
<c:if test="${not empty labelKey}"><c:set var="label"><spring:message code="${labelKey}"/></c:set></c:if>

<div class="dataentry">
	<c:if test="${not empty helpText}"><form:label path="${field}"><tags:help text="${helpText}" title="${helpTitle}"><spring:message code="${labelKey}"/></tags:help></form:label></c:if>
	<c:if test="${empty helpText}"><form:label path="${field}"><spring:message code="${labelKey}"/></form:label></c:if>
	
	<form:checkbox path="${field}" />
</div>