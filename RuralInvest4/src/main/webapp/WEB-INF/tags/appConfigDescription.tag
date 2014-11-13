<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ attribute name="ac" required="true" type="riv.objects.config.AppConfig" %>
<c:choose>
	<c:when test="${ac.configId gt 0 or (ac.configId lt -30 and ac.configId gt -34)}">${ac.description}</c:when>
	<c:when test="${ac.configId eq -4 }"><spring:message code="fieldOffice.generic"/></c:when>
	<c:when test="${ac.configId eq -7 }"><spring:message code="projectStatus.generic"/></c:when>
	<c:when test="${ac.configId eq -20 }"><spring:message code="projectStatus.proposal"/></c:when>
	<c:when test="${ac.configId eq -21 }"><spring:message code="projectStatus.approved"/></c:when>
	<c:when test="${ac.configId eq -22 }"><spring:message code="projectStatus.investment"/></c:when>
	<c:when test="${ac.configId eq -23 }"><spring:message code="projectStatus.operational"/></c:when>
	<c:when test="${ac.configId eq -5 }"><spring:message code="projectCategory.generic.ig"/></c:when>
	<c:when test="${ac.configId eq -2 }"><spring:message code="projectCategory.generic.nig"/></c:when>
	<c:when test="${ac.configId eq -3 }"><spring:message code="beneficiary.generic"/></c:when>
	<c:when test="${ac.configId eq -6 }"><spring:message code="enviroCategory.generic"/></c:when>
	<c:when test="${ac.configId eq -8 }"><spring:message code="customFields.appConfig1.generic"/></c:when>
	<c:when test="${ac.configId eq -9 }"><spring:message code="customFields.appConfig2.generic"/></c:when>
</c:choose>