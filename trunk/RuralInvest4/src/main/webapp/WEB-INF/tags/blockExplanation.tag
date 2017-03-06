<%@ include file="/WEB-INF/jsp/inc/include.jsp" %><%@ attribute name="block" required="true" type="riv.objects.ProductOrBlock" %>
- <spring:message code="misc.per.unitType"/> ${block.unitType}
<c:if test="${block.cycles}">
<c:set var="lengthUnitText">
	<c:if test="${block.lengthUnit==0}"><spring:message code="units.months"/></c:if>
	<c:if test="${block.lengthUnit==1}"><spring:message code="units.weeks"/></c:if>
	<c:if test="${block.lengthUnit==2}"><spring:message code="units.days.calendar"/></c:if>
	<c:if test="${block.lengthUnit==3}"><spring:message code="units.days.week"/></c:if>
</c:set>
<c:set var="unitLengthPer"><c:if test="${block.cycleLength eq 1}"><spring:message code="misc.per.cycleLength"/></c:if><c:if test="${block.cycleLength gt 1}"><spring:message code="misc.per.cycleLength.many"/></c:if></c:set>
 ${unitLengthPer} ${block.cycleLength} ${lengthUnitText}
 </c:if>