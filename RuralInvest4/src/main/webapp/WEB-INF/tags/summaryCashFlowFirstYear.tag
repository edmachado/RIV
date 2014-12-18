<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<div id="summaryCashFlowFirstYear" class="summary" title='<spring:message code="project.report.cashFlowFirst"/> '>
	<spring:message code="project.report.summaryTables.info"/> <spring:message code="project.report.cashFlowFirst"/>
	<a href="../../report/${project.projectId}/projectCashFlowFirst.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
	<a href="../../report/${project.projectId}/projectCashFlowFirst.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> 
	<tags:table>
		<display:table list="${firstYearSummary}" id="row" requestURI=""  cellspacing="0" cellpadding="0"
			export="false" htmlId="firstYearSummary">
			<display:column title="" class="left">
				<c:choose>
					<c:when test="${row_rowNum eq 1}">
						<spring:message code="project.report.cashFlowFirst.operIncomes"/>
					</c:when>
					<c:when test="${row_rowNum eq 2}">
						<spring:message code="project.report.cashFlowFirst.operCosts"/>
					</c:when>
					<c:when test="${row_rowNum eq 3}">
						<spring:message code="project.report.cashFlowFirst.maint"/>
					</c:when>
					<c:when test="${row_rowNum eq 4}">
						<spring:message code="project.report.cashFlowFirst.general"/>
					</c:when>
					<c:when test="${row_rowNum eq 5}">
						<b><spring:message code="project.report.summary.total"/></b>
					</c:when>
					<c:when test="${row_rowNum eq 6}">
						<b><spring:message code="project.report.summary.cumulative"/></b>
					</c:when>
				</c:choose>
			</display:column>
			<c:forEach var="i" begin="0" end="11">
				<c:set var="title">
					<c:if test="${i+project.startupMonth<=12}"><spring:message code="calendar.month.${i+project.startupMonth}"/></c:if>
					<c:if test="${i+project.startupMonth>12}"><spring:message code="calendar.month.${(i+project.startupMonth)%12}"/></c:if>
				</c:set>
				<c:set var="style"><c:if test="${row_rowNum gt 4}">font-weight:bold;</c:if></c:set>
				<display:column title="${title}"  style="${style }">
					<tags:formatCurrency value="${row[i]}"/>
				</display:column>
			</c:forEach>
		</display:table>
	</tags:table>
</div>