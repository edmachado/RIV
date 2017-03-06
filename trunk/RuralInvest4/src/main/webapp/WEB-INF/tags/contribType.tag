<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="type" required="true" %>
<c:choose>
	<c:when test="${type=='0'}"><spring:message code="projectContribution.contribType.govtCentral"/></c:when>
	<c:when test="${type=='1'}"><spring:message code="projectContribution.contribType.govtLocal"/></c:when>
	<c:when test="${type=='2'}"><spring:message code="projectContribution.contribType.ngoLocal"/></c:when>
	<c:when test="${type=='3'}"><spring:message code="projectContribution.contribType.ngoIntl"/></c:when>
	<c:when test="${type=='5'}"><spring:message code="projectContribution.contribType.beneficiary"/></c:when>
	<c:when test="${type=='4'}"><spring:message code="projectContribution.contribType.other"/></c:when>
</c:choose>