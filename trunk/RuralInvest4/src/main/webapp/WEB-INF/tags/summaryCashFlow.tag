<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<c:if test="${project.incomeGen}">
	<c:set var="title"><spring:message code="project.report.cashFlow"/></c:set>
	<c:set var="report">projectCashFlow</c:set>
</c:if>
<c:if test="${not project.incomeGen}">
	<c:set var="title"><spring:message code="project.report.cashFlowNongen"/></c:set>
	<c:set var="report">projectCashFlowNongen</c:set>
</c:if>
<div id="summaryCashFlow" class="summary" title="${title}">
	<spring:message code="project.report.summaryTables.info"/> ${title}
	<a href="../../report/${project.projectId}/${report}.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
	<a href="../../report/${project.projectId}/${report}.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a>	
	
	<br/><br/>
	<c:if test="${project.withWithout}"><h3><spring:message code="project.with" /></h3></c:if>
	<tags:table>
			<display:table list="${cashFlowSummary}" id="row" requestURI=""  cellspacing="0" cellpadding="0" export="false" htmlId="cashFlowSummary">
				<display:column title="" class="left">
					<c:choose>
						<c:when test="${row_rowNum eq 1}">
							<spring:message code="reference.incomes"/>
						</c:when>
						<c:when test="${row_rowNum eq 2}">
							<spring:message code="project.report.cashFlowNongen.costs"/>
						</c:when>
						<c:when test="${row_rowNum eq 3}">
							<b><spring:message code="project.report.summary.cashFlow.total"/></b>
						</c:when>
						<c:when test="${row_rowNum eq 4}">
							<b><spring:message code="project.report.summary.cashFlow.cumulative"/></b>
						</c:when>
					</c:choose>
				</display:column>
				<c:forEach var="i" begin="0" end="${project.duration-1}">
					<display:column title="${i+1}"  style="${style }">
						<tags:formatCurrency value="${row[i]}" noDecimals="true"/>
					</display:column>
				</c:forEach>
			</display:table>
		</tags:table>
		
		<c:if test="${project.withWithout}">
			<h3><spring:message code="project.without" /></h3>
			<tags:table>
				<display:table list="${cashFlowSummaryWithout}" id="wo" requestURI=""  cellspacing="0" cellpadding="0" export="false" htmlId="cashFlowSummary">
					<display:column title="" class="left">
						<c:choose>
							<c:when test="${wo_rowNum eq 1}">
								<spring:message code="reference.incomes"/>
							</c:when>
							<c:when test="${wo_rowNum eq 2}">
								<spring:message code="project.report.cashFlowNongen.costs"/>
							</c:when>
							<c:when test="${wo_rowNum eq 3}">
								<b><spring:message code="project.report.summary.cashFlow.total"/></b>
							</c:when>
							<c:when test="${wo_rowNum eq 4}">
								<b><spring:message code="project.report.summary.cashFlow.cumulative"/></b>
							</c:when>
						</c:choose>
					</display:column>
					<c:forEach var="i" begin="0" end="${project.duration-1}">
						<display:column title="${i+1}"  style="${style }">
							<tags:formatCurrency value="${wo[i]}" noDecimals="true"/>
						</display:column>
					</c:forEach>
				</display:table>
			</tags:table>
		</c:if>
	</div>