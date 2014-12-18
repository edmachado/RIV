<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<script>
function showWcExplain() {
	$('#summaryWc').dialog({
		resizable: true, height:250, width:800
	});
};
</script>
<div id="summaryWc" title='<spring:message code="project.workingCapital.how"/>' class="summary">
	<p><spring:message code="project.workingCapital.explain"/>
		<c:if test="${empty project.wizardStep}">
		<%-- 	<spring:message code="project.report.cashFlowFirst"/>  --%>
			<a href="../../report/${project.projectId}/projectCashFlowFirst.pdf" target="_blank"><img src="../../img/pdf.gif" alt="PDF" title="PDF" border="0"> PDF</a>
			<a href="../../report/${project.projectId}/projectCashFlowFirst.xlsx" target="_blank"><img src="../../img/xls.gif" alt="Excel" title="Excel" border="0"> Excel</a>
		</c:if>
	</p>
	<br/>
	
	<tags:table>
		<table id="wcSummary" cellspacing="0" cellpadding="0">
			<thead>
				<tr>
					<c:forEach var="i" begin="0" end="11">
						<c:set var="monthClass"><c:if test="${i+1 le project.wcFinancePeriod}">period</c:if></c:set>
						<th class="${monthClass}">
							<c:if test="${i+project.startupMonth<=12}"><spring:message code="calendar.month.${i+project.startupMonth}"/></c:if>
							<c:if test="${i+project.startupMonth>12}"><spring:message code="calendar.month.${(i+project.startupMonth)%12}"/></c:if>
						</th> 
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<tr class="odd">
					<c:forEach var="month" items="${firstYear}">
						<c:set var="valueFormatted"><tags:formatCurrency value="${month}" /></c:set>
						<c:set var="requiredFormatted"><tags:formatCurrency value="${project.wcAmountRequired*-1}" /></c:set>
						<c:set var="monthClass2"><c:if test="${valueFormatted eq requiredFormatted}">highMonth</c:if></c:set>
						<td class="${monthClass2}">
						${valueFormatted}
						</td>
					</c:forEach>
				</tr>
			</tbody>
		</table>
	</tags:table>
	
	<p><span class="explain highMonth"><b><spring:message code="project.amtRequired"/>:</b></span> <spring:message code="project.amtRequired.explain"/></p> 
	<br/>
	<p><span class="explain period"><b><spring:message code="project.period"/>:</b></span> <spring:message code="project.period.help"/></p>
</div>