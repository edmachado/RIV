<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<div id="summaryBlocks" class="summary" title='<spring:message code="project.report.summary.block"/> '>
	<spring:message code="project.report.summaryTables.info"/> 
	<c:if test="${project.incomeGen}">
		<spring:message code="project.report.blockDetail"/>
		<a href="../../report/${project.projectId}/projectBlock.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
		<a href="../../report/${project.projectId}/projectBlocks.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> 
		<h2><spring:message code="project.report.summary.block.all"/></h2>
	</c:if>
	<c:if test="${not project.incomeGen}">
		<spring:message code="project.report.activityDetail"/>
		<a href="../../report/${project.projectId}/projectBlock.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
		<a href="../../report/${project.projectId}/projectBlocks.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0">${space}Excel</a> 
		<h2><spring:message code="project.report.summary.block.all.nongen"/></h2>	
	</c:if>
	
	<tags:table>
		<display:table list="${project.allBlockSummary}" id="row" cellpadding="0" cellspacing="0">
			<display:column class="left">
				<c:choose>
					<c:when test="${row_rowNum eq 1}"><spring:message code="project.report.summary.income"/></c:when>
					<c:when test="${row_rowNum eq 2}"><spring:message code="project.report.summary.costs"/></c:when>
					<c:when test="${row_rowNum eq 3}"><spring:message code="project.report.summary.total"/></c:when>
					<c:when test="${row_rowNum eq 4}"><spring:message code="project.report.summary.cumulative"/></c:when>
				</c:choose>
			</display:column>
			<c:forEach var="year" begin="1" end="${project.duration}">
			<display:column title="${year}">
				<tags:formatCurrency value="${row[year-1]}" noDecimals="true"/>
			</display:column>
			</c:forEach>
		</display:table>
	</tags:table>

	<c:forEach var="b" items="${project.blocks}">
		<h2>${b.description}</h2>
		<tags:table>
			<display:table list="${b.blockSummary}" id="row" cellpadding="0" cellspacing="0">
				<display:column class="left">
					<c:choose>
						<c:when test="${row_rowNum eq 1}"><spring:message code="project.report.summary.income"/></c:when>
						<c:when test="${row_rowNum eq 2}"><spring:message code="project.report.summary.costs"/></c:when>
						<c:when test="${row_rowNum eq 3}"><spring:message code="project.report.summary.total"/></c:when>
						<c:when test="${row_rowNum eq 4}"><spring:message code="project.report.summary.cumulative"/></c:when>
					</c:choose>
				</display:column>
				<c:forEach var="year" begin="1" end="${project.duration}">
					<display:column title="${year}">
						<tags:formatCurrency value="${row[year-1]}" noDecimals="true"/>
					</display:column>
				</c:forEach>
			</display:table>
		</tags:table>
	</c:forEach>
</div>