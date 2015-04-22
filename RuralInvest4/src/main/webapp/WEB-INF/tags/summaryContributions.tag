<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<div id="summaryContributions" class="summary" title='<spring:message code="project.report.contributionSummary"/>'>
	<i>... waiting for RIV team input ...</i>
	<h3>TOTALS</h3>
	<tags:summaryContributionsTable donorOrder="${fn:length(project.donors)}"/>
	<c:forEach var="donor" items="${project.donors}">
		<c:if test="${not donor.notSpecified}">
			<h3><c:choose>
					<c:when test="${donor.description eq 'state-public'}"><spring:message code="project.donor.statePublic"/></c:when>
					<c:otherwise>${donor.description}</c:otherwise>
			</c:choose></h3>
			<tags:summaryContributionsTable donorOrder="${donor.orderBy}"/>
		</c:if>
		<c:if test="${donor.notSpecified}"><c:set var="notSpec" value="${donor.orderBy}"/></c:if>
	</c:forEach>
	<h3><spring:message code="project.donor.notSpecified"/></h3>
	<tags:summaryContributionsTable donorOrder="${notSpec}"/>
</div>