<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<div id="summaryProfitability" class="summary" title='<spring:message code="project.report.profitability"/> '>
	<spring:message code="project.report.summaryTables.info"/> <spring:message code="project.report.profitability"/>
	<a href="../../report/${project.projectId}/projectProfitability.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
	<a href="../../report/${project.projectId}/projectProfitability.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0"> Excel</a> 
	<br/><br/>
	<c:if test="${project.withWithout}"><h3><spring:message code="project.incremental"/></h3></c:if>			
	<tags:table>
		<display:table list="${profitabilitySummary}" id="row" requestURI=""  cellspacing="0" cellpadding="0"
			export="false" htmlId="profitabilitySummary">
			<display:column title="" class="left">
				<c:choose>
					<c:when test="${row_rowNum eq 1}">
						<spring:message code="project.report.profitability.incomes"/>
					</c:when>
					<c:when test="${row_rowNum eq 2}">
						<spring:message code="project.report.profitability.costs"/>
					</c:when>
					<c:when test="${row_rowNum eq 3}">
						<spring:message code="project.report.profitability.donations"/>
					</c:when>
					<c:when test="${row_rowNum eq 4}">
						<b><spring:message code="project.report.profitability.donations.netAfter"/></b>
					</c:when>
					<c:when test="${row_rowNum eq 5}">
						<b><spring:message code="project.report.summary.cumulative"/></b>
					</c:when>
				</c:choose>
			</display:column>
			<c:forEach var="i" begin="0" end="${project.duration-1}">
				<display:column title="${i+1}">
					<tags:formatCurrency value="${row[i]}" noDecimals="true"/>
				</display:column>
			</c:forEach>
		</display:table>
	</tags:table>
	
	<c:if test="${project.withWithout}">
		<h3><spring:message code="project.with"/></h3>
		<tags:table>
			<display:table list="${profitabilitySummaryWith}" id="rowWith" requestURI=""  cellspacing="0" cellpadding="0"
				export="false" htmlId="profitabilitySummary">
				<display:column title="" class="left">
					<c:choose>
						<c:when test="${rowWith_rowNum eq 1}">
							<spring:message code="project.report.profitability.incomes"/>
						</c:when>
						<c:when test="${rowWith_rowNum eq 2}">
							<spring:message code="project.report.profitability.costs"/>
						</c:when>
						<c:when test="${rowWith_rowNum eq 3}">
							<spring:message code="project.report.profitability.donations"/>
						</c:when>
						<c:when test="${rowWith_rowNum eq 4}">
							<b><spring:message code="project.report.profitability.donations.netAfter"/></b>
						</c:when>
						<c:when test="${rowWith_rowNum eq 5}">
							<b><spring:message code="project.report.summary.cumulative"/></b>
						</c:when>
					</c:choose>
				</display:column>
				<c:forEach var="i" begin="0" end="${project.duration-1}">
					<display:column title="${i+1}">
						<tags:formatCurrency value="${rowWith[i]}" noDecimals="true"/>
					</display:column>
				</c:forEach>
			</display:table>
		</tags:table>
	
		
		<h3><spring:message code="project.without"/></h3>
		<tags:table>
			<display:table list="${profitabilitySummaryWithout}" id="rowWithout" requestURI=""  cellspacing="0" cellpadding="0"
				export="false" htmlId="profitabilitySummary">
				<display:column title="" class="left">
					<c:choose>
						<c:when test="${rowWithout_rowNum eq 1}">
							<spring:message code="project.report.profitability.incomes"/>
						</c:when>
						<c:when test="${rowWithout_rowNum eq 2}">
							<spring:message code="project.report.profitability.costs"/>
						</c:when>
						<c:when test="${rowWithout_rowNum eq 3}">
							<spring:message code="project.report.profitability.donations"/>
						</c:when>
						<c:when test="${rowWithout_rowNum eq 4}">
							<b><spring:message code="project.report.profitability.donations.netAfter"/></b>
						</c:when>
						<c:when test="${rowWithout_rowNum eq 5}">
							<b><spring:message code="project.report.summary.cumulative"/></b>
						</c:when>
					</c:choose>
				</display:column>
				<c:forEach var="i" begin="0" end="${project.duration-1}">
					<display:column title="${i+1}">
						<tags:formatCurrency value="${rowWithout[i]}" noDecimals="true"/>
					</display:column>
				</c:forEach>
			</display:table>
		</tags:table>
	</c:if>
</div>